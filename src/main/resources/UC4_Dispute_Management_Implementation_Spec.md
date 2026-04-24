# UC4 — Dispute Management using Agentic AI
## Spring Boot Demo Application — Coding Agent Implementation Spec

> **Goal:** A self-contained Spring Boot demo that showcases a ReAct-driven multi-agent dispute resolution system with explainability, audit trails, and human-in-the-loop routing. Every AI concept must be visually demonstrable.

---

## 1. Project Overview

### What to Build
A REST + Web UI Spring Boot application that simulates two dispute scenarios:
1. **Scenario 1 — Fraudulent Transaction (Auto-Decision):** System automatically detects anomaly and resolves without human intervention.
2. **Scenario 2 — Merchant Dispute (Human-in-the-Loop):** System collects context, cannot auto-decide, routes to a human review queue.

### Non-Goals (Keep it Simple)
- No real banking integration
- No real payment gateway
- No persistent database required (in-memory H2 or even `ConcurrentHashMap` is fine)
- No authentication/authorization
- No production-grade error handling

---

## 2. Tech Stack

| Layer | Technology |
|---|---|
| Framework | Spring Boot 3.x |
| Build | Maven or Gradle |
| AI / LLM | Anthropic Claude API (`claude-sonnet-4-20250514`) via raw HTTP (`RestTemplate` or `WebClient`) |
| Persistence | In-memory (`ConcurrentHashMap` or H2 with JPA) |
| UI | Single-page Thymeleaf + Bootstrap 5 (or plain HTML served as static) |
| API style | REST JSON |

---

## 3. Project Structure

```
dispute-management-demo/
├── pom.xml
├── src/
│   └── main/
│       ├── java/com/demo/dispute/
│       │   ├── DisputeApplication.java
│       │   │
│       │   ├── config/
│       │   │   └── AnthropicConfig.java          # API key, RestTemplate bean
│       │   │
│       │   ├── model/
│       │   │   ├── DisputeRequest.java            # Input DTO
│       │   │   ├── DisputeCase.java               # Core domain object
│       │   │   ├── AgentStep.java                 # Single ReAct step (Think/Act/Observe)
│       │   │   ├── AuditTrail.java                # Ordered list of AgentSteps
│       │   │   └── DisputeStatus.java             # Enum: PENDING, AUTO_RESOLVED, ESCALATED
│       │   │
│       │   ├── agent/
│       │   │   ├── DisputeOrchestratorAgent.java  # Master agent — decides routing
│       │   │   ├── FraudDetectionAgent.java       # Tool: checks fraud signals
│       │   │   ├── TransactionDataAgent.java      # Tool: fetches mock transaction data
│       │   │   ├── MerchantContextAgent.java      # Tool: fetches mock merchant data
│       │   │   └── ComplianceAgent.java           # Tool: validates decision vs policy
│       │   │
│       │   ├── service/
│       │   │   ├── DisputeService.java            # Orchestrates the full flow
│       │   │   ├── ClaudeApiService.java          # Wraps Anthropic API calls
│       │   │   └── HumanReviewService.java        # Manages the human review queue
│       │   │
│       │   ├── controller/
│       │   │   ├── DisputeController.java         # POST /api/disputes
│       │   │   ├── HumanReviewController.java     # GET/POST /api/review
│       │   │   └── UiController.java              # Serves the demo HTML pages
│       │   │
│       │   └── store/
│       │       └── InMemoryDisputeStore.java      # ConcurrentHashMap-based storage
│       │
│       └── resources/
│           ├── application.properties
│           ├── static/
│           │   └── (Bootstrap CDN, minimal custom CSS)
│           └── templates/
│               ├── index.html                     # Home: submit a dispute
│               ├── result.html                    # Show audit trail + decision
│               └── review-queue.html              # Human reviewer dashboard
```

---

## 4. Domain Models

### 4.1 `DisputeRequest.java`
```java
public class DisputeRequest {
    private String disputeType;       // "FRAUD" or "MERCHANT"
    private String transactionId;
    private double amount;
    private String currency;          // "INR"
    private String description;       // e.g., "Transaction in unknown foreign location"
    private String merchantName;      // for MERCHANT disputes
    private String customerNote;      // free text from customer
}
```

### 4.2 `DisputeCase.java`
```java
public class DisputeCase {
    private String caseId;            // UUID
    private DisputeRequest request;
    private DisputeStatus status;
    private String finalDecision;     // "AUTO_APPROVED", "ESCALATED_TO_HUMAN", "REJECTED"
    private String explanation;       // Why the decision was made
    private AuditTrail auditTrail;
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;
    // Getters, setters, constructor
}
```

### 4.3 `AgentStep.java`
```java
public class AgentStep {
    private int stepNumber;
    private String agentName;         // e.g., "OrchestratorAgent", "FraudDetectionAgent"
    private String phase;             // "THINK", "ACT", "OBSERVE"
    private String thought;           // What the agent reasoned
    private String action;            // What tool/API it called
    private String observation;       // What came back
    private LocalDateTime timestamp;
}
```

### 4.4 `DisputeStatus.java`
```java
public enum DisputeStatus {
    PENDING,
    IN_PROGRESS,
    AUTO_RESOLVED,
    ESCALATED_TO_HUMAN,
    HUMAN_RESOLVED
}
```

---

## 5. Agent Design (ReAct Pattern)

Each agent follows the **Think → Act → Observe** loop. This loop is what gets displayed in the UI to make the AI reasoning transparent.

### 5.1 `ClaudeApiService.java`

Wraps raw calls to the Anthropic API. Keep it simple — no SDK needed.

```java
@Service
public class ClaudeApiService {

    @Value("${anthropic.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;

    // Core method: send a prompt, get back text
    public String chat(String systemPrompt, String userMessage) {
        // POST to https://api.anthropic.com/v1/messages
        // Headers: x-api-key, anthropic-version: 2023-06-01, Content-Type: application/json
        // Body: { model: "claude-sonnet-4-20250514", max_tokens: 1024, system: systemPrompt, messages: [{role: user, content: userMessage}] }
        // Return: response.content[0].text
    }
}
```

**Request body structure:**
```json
{
  "model": "claude-sonnet-4-20250514",
  "max_tokens": 1024,
  "system": "<system prompt>",
  "messages": [
    { "role": "user", "content": "<user message>" }
  ]
}
```

### 5.2 `DisputeOrchestratorAgent.java`

This is the main ReAct loop. It calls sub-agents as tools.

```java
@Component
public class DisputeOrchestratorAgent {

    // Inject: ClaudeApiService, FraudDetectionAgent, TransactionDataAgent,
    //         MerchantContextAgent, ComplianceAgent

    public DisputeCase process(DisputeRequest request) {

        DisputeCase disputeCase = new DisputeCase(/* init */);
        AuditTrail trail = new AuditTrail();

        // === STEP 1: THINK — Understand the dispute ===
        AgentStep step1 = new AgentStep();
        step1.setAgentName("OrchestratorAgent");
        step1.setPhase("THINK");
        String understanding = claudeApi.chat(
            ORCHESTRATOR_SYSTEM_PROMPT,
            "Analyze this dispute: " + toJson(request)
        );
        step1.setThought(understanding);
        trail.addStep(step1);

        // === STEP 2: ACT — Fetch transaction data ===
        AgentStep step2 = new AgentStep();
        step2.setPhase("ACT");
        step2.setAction("Calling TransactionDataAgent for txn " + request.getTransactionId());
        TransactionData txnData = transactionDataAgent.fetch(request.getTransactionId());
        step2.setObservation(toJson(txnData));
        trail.addStep(step2);

        // === STEP 3: ACT — Run fraud detection (for FRAUD disputes) ===
        if ("FRAUD".equals(request.getDisputeType())) {
            AgentStep step3 = new AgentStep();
            step3.setPhase("ACT");
            step3.setAction("Calling FraudDetectionAgent");
            FraudSignals signals = fraudAgent.analyze(request, txnData);
            step3.setObservation("Fraud score: " + signals.getScore() + " | Signals: " + signals.getSignalsList());
            trail.addStep(step3);

            // === STEP 4: THINK — Make decision ===
            AgentStep step4 = new AgentStep();
            step4.setPhase("THINK");
            String decision = claudeApi.chat(
                DECISION_SYSTEM_PROMPT,
                "Given fraud signals: " + toJson(signals) + " and transaction: " + toJson(txnData) +
                " — should this dispute be auto-approved? Respond with: DECISION: <AUTO_APPROVED|ESCALATED> REASON: <explanation>"
            );
            step4.setThought(decision);
            trail.addStep(step4);

            // Parse decision and set on case
            parseAndSetDecision(disputeCase, decision);

        } else {
            // MERCHANT dispute — always route to human after collecting context
            // ... fetch merchant context, delivery data ...
            // Final step always sets status = ESCALATED_TO_HUMAN
        }

        // === FINAL STEP: Compliance check ===
        AgentStep complianceStep = new AgentStep();
        complianceStep.setAgentName("ComplianceAgent");
        complianceStep.setPhase("ACT");
        String complianceResult = complianceAgent.validate(disputeCase);
        complianceStep.setObservation(complianceResult);
        trail.addStep(complianceStep);

        disputeCase.setAuditTrail(trail);
        return disputeCase;
    }
}
```

### 5.3 Sub-Agent Contracts

Each sub-agent is a simple `@Component` that returns a POJO. They simulate tool use — in a real system these would call real APIs.

#### `FraudDetectionAgent.java`
```java
@Component
public class FraudDetectionAgent {
    // Inject ClaudeApiService

    public FraudSignals analyze(DisputeRequest request, TransactionData txnData) {
        // Ask Claude to reason about fraud signals based on transaction context
        String prompt = """
            You are a fraud detection engine. Analyze the following transaction data and return fraud signals.
            Transaction: %s
            Customer description: %s
            
            Respond in JSON: { "score": 0-100, "signals": ["signal1", "signal2"], "recommendation": "..." }
            """.formatted(toJson(txnData), request.getDescription());

        String response = claudeApi.chat(FRAUD_SYSTEM_PROMPT, prompt);
        return parseFraudSignals(response);
    }
}
```

#### `TransactionDataAgent.java`
```java
@Component
public class TransactionDataAgent {
    // Returns MOCK data — hardcoded or randomly generated for demo

    public TransactionData fetch(String txnId) {
        // Return mock data: amount, location, merchant, timestamp, customer travel history flag
        return TransactionData.builder()
            .transactionId(txnId)
            .location("Unknown Foreign Country")
            .hasTravelHistory(false)
            .isFirstTimeLocation(true)
            .build();
    }
}
```

#### `MerchantContextAgent.java`
```java
@Component
public class MerchantContextAgent {
    // Returns MOCK merchant + delivery data

    public MerchantContext fetch(String merchantName) {
        return MerchantContext.builder()
            .merchantName(merchantName)
            .deliveryStatus("NOT_DELIVERED")
            .lastKnownDeliveryAttempt("3 days ago")
            .disputeHistory(2) // previous disputes
            .build();
    }
}
```

#### `ComplianceAgent.java`
```java
@Component
public class ComplianceAgent {
    // Validates the decision against policy rules

    public String validate(DisputeCase disputeCase) {
        String prompt = """
            You are a banking compliance officer. Review this dispute decision for policy compliance.
            Decision: %s
            Reason: %s
            Dispute Type: %s
            
            Check: (1) Is the auto-decision threshold appropriate? (2) Is audit trail complete? (3) Any regulatory concerns?
            Respond briefly in 2-3 sentences.
            """.formatted(disputeCase.getFinalDecision(), disputeCase.getExplanation(),
                          disputeCase.getRequest().getDisputeType());

        return claudeApi.chat(COMPLIANCE_SYSTEM_PROMPT, prompt);
    }
}
```

---

## 6. System Prompts

Define these as constants in each agent or in a `Prompts.java` class.

```java
public class Prompts {

    public static final String ORCHESTRATOR_SYSTEM_PROMPT = """
        You are an intelligent dispute resolution orchestrator for a banking system.
        Your role is to analyze customer disputes, gather evidence through available tools,
        and make fair, explainable decisions.
        Always reason step by step before making decisions.
        For fraud cases with strong signals, you may auto-resolve.
        For merchant disputes with ambiguity, always escalate to human review.
        """;

    public static final String FRAUD_SYSTEM_PROMPT = """
        You are a fraud detection AI for a bank.
        Analyze transaction patterns and return structured fraud signals.
        Always respond in valid JSON only.
        """;

    public static final String DECISION_SYSTEM_PROMPT = """
        You are a dispute decision engine.
        Based on evidence provided, make a clear decision.
        Format your response exactly as:
        DECISION: <AUTO_APPROVED or ESCALATED>
        REASON: <one clear sentence explaining why>
        CONFIDENCE: <HIGH, MEDIUM, or LOW>
        """;

    public static final String COMPLIANCE_SYSTEM_PROMPT = """
        You are a banking compliance officer.
        Review dispute decisions for policy adherence and regulatory compliance.
        Be concise and specific.
        """;
}
```

---

## 7. REST API

### `DisputeController.java`

```
POST /api/disputes
Content-Type: application/json
Body: DisputeRequest JSON

Response: DisputeCase JSON (with full audit trail)
```

### `HumanReviewController.java`

```
GET  /api/review/queue              → List<DisputeCase> where status=ESCALATED_TO_HUMAN
GET  /api/review/{caseId}           → DisputeCase detail
POST /api/review/{caseId}/decision  → Body: { decision: "APPROVED"|"REJECTED", note: "..." }
                                       Updates case status to HUMAN_RESOLVED
```

### `UiController.java`

```
GET /           → index.html (dispute submission form)
GET /result     → result.html (shows case detail + audit trail)
GET /review     → review-queue.html (human reviewer dashboard)
```

---

## 8. Web UI Pages

Keep the UI minimal but visually clear for demo purposes.

### 8.1 `index.html` — Submit Dispute

Two pre-filled demo buttons for each scenario:

```html
<!-- Scenario 1 button: pre-fills form with fraud transaction data -->
<button onclick="loadScenario1()">Load Scenario 1: Fraudulent Transaction</button>

<!-- Scenario 2 button: pre-fills form with merchant dispute data -->
<button onclick="loadScenario2()">Load Scenario 2: Merchant Dispute</button>

<!-- Form fields: disputeType, transactionId, amount, description, merchantName, customerNote -->
<!-- On submit: POST /api/disputes, redirect to /result?caseId=... -->
```

**Scenario 1 pre-fill values:**
```javascript
function loadScenario1() {
    document.getElementById("disputeType").value = "FRAUD";
    document.getElementById("transactionId").value = "TXN-" + Date.now();
    document.getElementById("amount").value = "25000";
    document.getElementById("description").value = "₹25,000 debit card transaction in an unknown location — foreign country with no travel history";
    document.getElementById("customerNote").value = "I did not make this transaction. I was at home in Mumbai.";
}
```

**Scenario 2 pre-fill values:**
```javascript
function loadScenario2() {
    document.getElementById("disputeType").value = "MERCHANT";
    document.getElementById("transactionId").value = "TXN-" + Date.now();
    document.getElementById("amount").value = "8000";
    document.getElementById("merchantName").value = "QuickShop India";
    document.getElementById("description").value = "₹8,000 e-commerce charge — Item not delivered";
    document.getElementById("customerNote").value = "Ordered 2 weeks ago. No delivery. Merchant not responding.";
}
```

### 8.2 `result.html` — Audit Trail + Decision

This page is the key demo screen. It must show:

1. **Decision Banner** — Large coloured badge: `AUTO_APPROVED` (green) or `ESCALATED_TO_HUMAN` (orange)
2. **Explanation** — One-sentence reason from AI
3. **Agent Interaction Flow** — Horizontal or vertical timeline showing each agent that participated
4. **Audit Trail Table** — Step-by-step table:

| Step | Agent | Phase | Thought / Action | Observation | Time |
|------|-------|-------|-----------------|-------------|------|

5. **Compliance Note** — Box with compliance agent output

```html
<!-- Decision banner -->
<div class="alert alert-success" th:if="${case.status == 'AUTO_RESOLVED'}">
    ✅ AUTO RESOLVED — <span th:text="${case.explanation}"></span>
</div>
<div class="alert alert-warning" th:if="${case.status == 'ESCALATED_TO_HUMAN'}">
    🔄 ESCALATED TO HUMAN REVIEW — <span th:text="${case.explanation}"></span>
</div>

<!-- Agent flow diagram (simple Bootstrap badges + arrows) -->
<div class="agent-flow">
    OrchestratorAgent → TransactionDataAgent → FraudDetectionAgent → ComplianceAgent → [Decision]
</div>

<!-- Audit trail -->
<table class="table table-striped">
    <thead><tr><th>#</th><th>Agent</th><th>Phase</th><th>Detail</th><th>Time</th></tr></thead>
    <tbody>
        <tr th:each="step : ${case.auditTrail.steps}">
            <td th:text="${step.stepNumber}"></td>
            <td><span class="badge bg-primary" th:text="${step.agentName}"></span></td>
            <td>
                <span class="badge bg-info" th:if="${step.phase == 'THINK'}">💭 THINK</span>
                <span class="badge bg-warning" th:if="${step.phase == 'ACT'}">⚡ ACT</span>
                <span class="badge bg-success" th:if="${step.phase == 'OBSERVE'}">👁 OBSERVE</span>
            </td>
            <td>
                <div th:if="${step.thought}" th:text="${step.thought}"></div>
                <div th:if="${step.action}"><strong>Action:</strong> <span th:text="${step.action}"></span></div>
                <div th:if="${step.observation}"><em th:text="${step.observation}"></em></div>
            </td>
            <td th:text="${step.timestamp}"></td>
        </tr>
    </tbody>
</table>
```

### 8.3 `review-queue.html` — Human Reviewer Dashboard

```html
<!-- Table of all ESCALATED cases -->
<table class="table">
    <thead><tr><th>Case ID</th><th>Type</th><th>Amount</th><th>Description</th><th>Actions</th></tr></thead>
    <tbody th:each="case : ${cases}">
        <tr>
            <td th:text="${case.caseId}"></td>
            <td th:text="${case.request.disputeType}"></td>
            <td th:text="${case.request.amount}"></td>
            <td th:text="${case.request.description}"></td>
            <td>
                <a th:href="@{/result(caseId=${case.caseId})}">View Details</a>
                <!-- Approve / Reject buttons that POST to /api/review/{caseId}/decision -->
                <button onclick="approve('...')">✅ Approve</button>
                <button onclick="reject('...')">❌ Reject</button>
            </td>
        </tr>
    </tbody>
</table>
```

---

## 9. Configuration

### `application.properties`
```properties
# Anthropic API
anthropic.api.key=${ANTHROPIC_API_KEY}
anthropic.api.url=https://api.anthropic.com/v1/messages
anthropic.model=claude-sonnet-4-20250514

# Server
server.port=8080
spring.application.name=dispute-management-demo

# Thymeleaf
spring.thymeleaf.cache=false
```

### `AnthropicConfig.java`
```java
@Configuration
public class AnthropicConfig {

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate rt = new RestTemplate();
        rt.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        return rt;
    }
}
```

---

## 10. `ClaudeApiService.java` — Full Implementation Guide

```java
@Service
public class ClaudeApiService {

    @Value("${anthropic.api.key}")
    private String apiKey;

    @Value("${anthropic.api.url}")
    private String apiUrl;

    @Value("${anthropic.model}")
    private String model;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public String chat(String systemPrompt, String userMessage) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-key", apiKey);
        headers.set("anthropic-version", "2023-06-01");
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("model", model);
        body.put("max_tokens", 1024);
        body.put("system", systemPrompt);
        body.put("messages", List.of(Map.of("role", "user", "content", userMessage)));

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, entity, Map.class);
            List<Map<String, Object>> content = (List<Map<String, Object>>) response.getBody().get("content");
            return (String) content.get(0).get("text");
        } catch (Exception e) {
            // For demo: return a fallback string so flow continues
            return "Agent response unavailable: " + e.getMessage();
        }
    }
}
```

---

## 11. `DisputeService.java` — Orchestration Entry Point

```java
@Service
public class DisputeService {

    // Inject: DisputeOrchestratorAgent, InMemoryDisputeStore

    public DisputeCase submit(DisputeRequest request) {
        // 1. Generate case ID
        String caseId = UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        // 2. Create initial case
        DisputeCase disputeCase = new DisputeCase();
        disputeCase.setCaseId(caseId);
        disputeCase.setRequest(request);
        disputeCase.setStatus(DisputeStatus.IN_PROGRESS);
        disputeCase.setCreatedAt(LocalDateTime.now());

        // 3. Run orchestrator (this calls Claude + sub-agents)
        disputeCase = orchestratorAgent.process(disputeCase);

        // 4. Store in memory
        store.save(disputeCase);

        return disputeCase;
    }

    public List<DisputeCase> getEscalatedCases() {
        return store.findByStatus(DisputeStatus.ESCALATED_TO_HUMAN);
    }

    public DisputeCase humanDecide(String caseId, String decision, String note) {
        DisputeCase c = store.findById(caseId);
        c.setStatus(DisputeStatus.HUMAN_RESOLVED);
        c.setFinalDecision(decision);
        c.setExplanation("Human reviewer decision: " + note);
        c.setResolvedAt(LocalDateTime.now());

        // Add a final audit step
        AgentStep humanStep = new AgentStep();
        humanStep.setAgentName("HumanReviewer");
        humanStep.setPhase("ACT");
        humanStep.setAction("Human reviewer submitted decision: " + decision);
        humanStep.setObservation(note);
        c.getAuditTrail().addStep(humanStep);

        store.save(c);
        return c;
    }
}
```

---

## 12. `InMemoryDisputeStore.java`

```java
@Component
public class InMemoryDisputeStore {

    private final ConcurrentHashMap<String, DisputeCase> store = new ConcurrentHashMap<>();

    public void save(DisputeCase disputeCase) {
        store.put(disputeCase.getCaseId(), disputeCase);
    }

    public DisputeCase findById(String caseId) {
        return store.get(caseId);
    }

    public List<DisputeCase> findAll() {
        return new ArrayList<>(store.values());
    }

    public List<DisputeCase> findByStatus(DisputeStatus status) {
        return store.values().stream()
            .filter(c -> c.getStatus() == status)
            .collect(Collectors.toList());
    }
}
```

---

## 13. Demo Flow (End-to-End)

### Scenario 1 — Fraudulent Transaction (Auto-Decision)

```
1. User opens localhost:8080
2. Clicks "Load Scenario 1" → form pre-filled with ₹25,000 fraud case
3. Clicks Submit → POST /api/disputes
4. OrchestratorAgent runs:
   THINK: "This is a fraud dispute. I need to gather transaction data and check for anomalies."
   ACT  : TransactionDataAgent → returns { location: "Unknown Country", hasTravelHistory: false }
   ACT  : FraudDetectionAgent  → returns { score: 92, signals: ["foreign_location", "no_travel_history", "unusual_amount"] }
   THINK: "Fraud score 92/100. Strong signals. Auto-approve this dispute."
   ACT  : ComplianceAgent      → "Decision is compliant with fraud policy threshold of 80+. Audit trail complete."
5. Case status = AUTO_RESOLVED, decision = AUTO_APPROVED
6. UI shows: ✅ AUTO RESOLVED banner + full audit trail table
```

### Scenario 2 — Merchant Dispute (Human-in-Loop)

```
1. User clicks "Load Scenario 2" → form pre-filled with ₹8,000 merchant dispute
2. Clicks Submit → POST /api/disputes
3. OrchestratorAgent runs:
   THINK: "Merchant dispute — insufficient info to auto-decide. Need merchant and delivery context."
   ACT  : TransactionDataAgent → returns { merchant: "QuickShop India", amount: 8000 }
   ACT  : MerchantContextAgent → returns { deliveryStatus: "NOT_DELIVERED", lastAttempt: "3 days ago" }
   THINK: "Delivery not confirmed. Merchant history shows 2 prior disputes. Cannot auto-resolve. Escalate."
   ACT  : ComplianceAgent      → "Escalation is correct per policy. Human review required for ambiguous merchant cases."
4. Case status = ESCALATED_TO_HUMAN
5. UI shows: 🔄 ESCALATED TO HUMAN REVIEW banner + audit trail
6. Human reviewer visits /review → sees the case → clicks Approve/Reject
7. Case updated to HUMAN_RESOLVED
```

---

## 14. Key Demo Talking Points (What Each Part Demonstrates)

| Concept | Where It's Visible |
|---|---|
| **ReAct Pattern** | Audit trail shows THINK → ACT → OBSERVE cycles |
| **Multi-Agent Orchestration** | Different agent names in audit trail (Orchestrator, Fraud, Merchant, Compliance) |
| **Auto Decision** | Scenario 1 resolves without human touch |
| **Human-in-Loop** | Scenario 2 routes to review queue; human makes final call |
| **Explainability** | Every decision has a `REASON` field shown on result page |
| **Audit Trail** | Full step-by-step table with timestamps |
| **Compliance Check** | ComplianceAgent always runs as final validation step |
| **Agent Interaction Flow** | Visual badge trail on result page showing agent sequence |

---

## 15. `pom.xml` Dependencies

```xml
<dependencies>
    <!-- Spring Boot Web -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <!-- Thymeleaf for UI -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-thymeleaf</artifactId>
    </dependency>

    <!-- Jackson for JSON -->
    <dependency>
        <groupId>com.fasterxml.jackson.core</groupId>
        <artifactId>jackson-databind</artifactId>
    </dependency>

    <!-- Lombok (optional, reduces boilerplate) -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>

    <!-- Test -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

---

## 16. Environment Setup

```bash
# 1. Set API key
export ANTHROPIC_API_KEY=sk-ant-...

# 2. Build and run
./mvnw spring-boot:run

# 3. Open browser
open http://localhost:8080
```

---

## 17. Implementation Checklist (for Coding Agent)

- [ ] Create Spring Boot project with dependencies in `pom.xml`
- [ ] Implement all domain models (`DisputeRequest`, `DisputeCase`, `AgentStep`, `AuditTrail`, `DisputeStatus`)
- [ ] Implement `ClaudeApiService` with `chat(systemPrompt, userMessage)` method
- [ ] Implement `AnthropicConfig` with `RestTemplate` bean
- [ ] Implement `InMemoryDisputeStore`
- [ ] Implement `TransactionDataAgent` (mock data)
- [ ] Implement `MerchantContextAgent` (mock data)
- [ ] Implement `FraudDetectionAgent` (calls Claude with fraud signals prompt)
- [ ] Implement `ComplianceAgent` (calls Claude with compliance review prompt)
- [ ] Implement `DisputeOrchestratorAgent` (full ReAct loop, branching on dispute type)
- [ ] Implement `DisputeService` (entry point, stores case)
- [ ] Implement `HumanReviewService`
- [ ] Implement `DisputeController` (POST /api/disputes)
- [ ] Implement `HumanReviewController` (GET queue, POST decision)
- [ ] Implement `UiController` (serves Thymeleaf pages)
- [ ] Create `index.html` with two scenario buttons and submission form
- [ ] Create `result.html` with decision banner + audit trail table + agent flow
- [ ] Create `review-queue.html` with escalated cases list + approve/reject
- [ ] Add `application.properties` with API config
- [ ] Test Scenario 1 end-to-end (should auto-resolve)
- [ ] Test Scenario 2 end-to-end (should escalate, then human-resolve)

---

## 18. Notes for Coding Agent

1. **Keep all AI calls in `ClaudeApiService`** — do not scatter HTTP calls elsewhere.
2. **Each sub-agent must add its own `AgentStep` to the audit trail** — this is what makes the demo visual.
3. **The OrchestratorAgent drives the ReAct loop** — sub-agents are tools it calls, not independent loops.
4. **Mock data agents must return realistic Indian banking context** — amounts in INR, merchant names, delivery scenarios.
5. **If Claude API call fails**, catch the exception and add a step with `observation = "Tool call failed: <reason>"` — do not crash the flow.
6. **The UI must work without JavaScript frameworks** — vanilla JS or Thymeleaf only.
7. **Scenario 1 must always auto-resolve** — hardcode mock data to produce fraud score > 80.
8. **Scenario 2 must always escalate** — hardcode delivery status as NOT_DELIVERED with ambiguous signals.
9. **All `AgentStep` timestamps must be recorded at the time of execution** — use `LocalDateTime.now()`.
10. **The `AuditTrail` steps list must be ordered** — use `List<AgentStep>` not `Set`.
