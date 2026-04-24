# Dispute Management System - AI-Powered Demo

A Spring Boot demonstration application showcasing an AI-powered dispute resolution system using the **ReAct (Reasoning and Acting)** pattern with multiple agents.

## 🎯 Overview

This application demonstrates:
- **Multi-Agent AI System**: Orchestrator, Fraud Detection, Transaction Data, Merchant Context, and Compliance agents
- **ReAct Pattern**: Think → Act → Observe loops for transparent AI reasoning
- **Auto-Decision**: High-confidence fraud cases resolved automatically
- **Human-in-the-Loop**: Ambiguous cases escalated to human reviewers
- **Complete Audit Trail**: Every decision step is logged and visible
- **Explainability**: Clear reasoning for every decision

## 🏗️ Architecture

```
DisputeOrchestratorAgent (Master)
    ├── TransactionDataAgent (Mock data)
    ├── FraudDetectionAgent (AI-powered)
    ├── MerchantContextAgent (Mock data)
    └── ComplianceAgent (AI-powered)
```

## 📋 Prerequisites

- **Java 17** or higher
- **Maven 3.6+**
- **LLM Provider** (choose one):
  - **Anthropic API Key** (Claude Sonnet 4) - Default
  - **Ollama** (local LLM) - Alternative option

## 🚀 Setup Instructions

### 1. Choose Your LLM Provider

The application supports two LLM providers:

#### Option A: Anthropic Claude (Default)

Set your Anthropic API key as an environment variable:

**macOS/Linux:**
```bash
export ANTHROPIC_API_KEY=sk-ant-your-api-key-here
```

**Windows (PowerShell):**
```powershell
$env:ANTHROPIC_API_KEY="sk-ant-your-api-key-here"
```

**Windows (CMD):**
```cmd
set ANTHROPIC_API_KEY=sk-ant-your-api-key-here
```

#### Option B: Ollama (Local LLM)

1. Install Ollama from https://ollama.ai
2. Pull a model (e.g., llama2):
   ```bash
   ollama pull llama2
   ```
3. Start Ollama service (usually runs on http://localhost:11434)
4. Update `application.properties`:
   ```properties
   llm.provider=ollama
   ollama.model=llama2
   ```

### 2. Build the Application

```bash
./mvnw clean install
```

### 3. Run the Application

```bash
./mvnw spring-boot:run
```

Or run the JAR directly:
```bash
java -jar target/dispute-management-demo-1.0.0.jar
```

### 4. Access the Application

Open your browser and navigate to:
```
http://localhost:8080
```

## 🎬 Demo Scenarios

### Scenario 1: Fraudulent Transaction (Auto-Decision)

**Setup:**
1. Click "🚨 Scenario 1: Fraudulent Transaction" button
2. Form auto-fills with:
   - Type: FRAUD
   - Amount: ₹25,000
   - Description: Transaction in unknown foreign location
   - Customer Note: "I did not make this transaction. I was at home in Mumbai."

**Expected Flow:**
1. OrchestratorAgent analyzes the dispute
2. TransactionDataAgent fetches transaction details
3. FraudDetectionAgent detects high fraud score (92/100)
4. System auto-approves the dispute
5. ComplianceAgent validates the decision

**Result:** ✅ AUTO RESOLVED

### Scenario 2: Merchant Dispute (Human-in-the-Loop)

**Setup:**
1. Click "📦 Scenario 2: Merchant Dispute" button
2. Form auto-fills with:
   - Type: MERCHANT
   - Amount: ₹8,000
   - Merchant: QuickShop India
   - Description: Item not delivered
   - Customer Note: "Ordered 2 weeks ago. No delivery. Merchant not responding."

**Expected Flow:**
1. OrchestratorAgent analyzes the dispute
2. TransactionDataAgent fetches transaction details
3. MerchantContextAgent fetches delivery status (NOT_DELIVERED)
4. System escalates to human review
5. ComplianceAgent validates the escalation

**Result:** 🔄 ESCALATED TO HUMAN REVIEW

**Human Review:**
1. Navigate to "View Human Review Queue"
2. Review the case details and AI recommendation
3. Click "View Full Audit Trail" to see complete reasoning
4. Approve or Reject with a note

## 📊 Key Features Demonstrated

### 1. ReAct Pattern Visualization
The audit trail shows each agent's:
- **💭 THINK**: Reasoning and analysis
- **⚡ ACT**: Tool calls and actions taken
- **👁 OBSERVE**: Results and observations

### 2. Agent Interaction Flow
Visual diagram showing the sequence of agents involved:
```
OrchestratorAgent → TransactionDataAgent → FraudDetectionAgent → ComplianceAgent → [Decision]
```

### 3. Complete Audit Trail
Every step is logged with:
- Step number
- Agent name
- Phase (THINK/ACT/OBSERVE)
- Detailed information
- Timestamp

### 4. Compliance Validation
Every decision is validated by the ComplianceAgent to ensure:
- Policy adherence
- Regulatory compliance
- Audit trail completeness

## 🛠️ API Endpoints

### REST API

**Submit Dispute:**
```
POST /api/disputes
Content-Type: application/json

{
  "disputeType": "FRAUD",
  "transactionId": "TXN-123",
  "amount": 25000,
  "currency": "INR",
  "description": "...",
  "customerNote": "..."
}
```

**Get Dispute:**
```
GET /api/disputes/{caseId}
```

**Get Review Queue:**
```
GET /api/review/queue
```

**Submit Human Decision:**
```
POST /api/review/{caseId}/decision
Content-Type: application/json

{
  "decision": "APPROVED",
  "note": "Verified with merchant. Customer claim is valid."
}
```

### Web UI

- **Home:** `http://localhost:8080/`
- **Result:** `http://localhost:8080/result?caseId={caseId}`
- **Review Queue:** `http://localhost:8080/review`

## 🔧 Configuration

Edit `src/main/resources/application.properties`:

```properties
# LLM Provider Configuration
# Options: "anthropic" or "ollama"
llm.provider=anthropic

# Anthropic API Configuration (used when llm.provider=anthropic)
anthropic.api.key=${ANTHROPIC_API_KEY:}
anthropic.api.url=https://api.anthropic.com/v1/messages
anthropic.model=claude-sonnet-4-20250514

# Ollama Configuration (used when llm.provider=ollama)
ollama.api.url=http://localhost:11434/api/generate
ollama.model=llama2

# Server
server.port=8080
```

### Switching Between Providers

**To use Anthropic Claude:**
```properties
llm.provider=anthropic
```

**To use Ollama:**
```properties
llm.provider=ollama
ollama.model=llama2  # or mistral, codellama, etc.
```

## 📁 Project Structure

```
src/main/java/com/demo/dispute/
├── DisputeApplication.java          # Main Spring Boot application
├── config/
│   ├── AnthropicConfig.java         # RestTemplate configuration
│   └── Prompts.java                 # System prompts for agents
├── model/
│   ├── DisputeRequest.java          # Input DTO
│   ├── DisputeCase.java             # Core domain object
│   ├── AgentStep.java               # ReAct step
│   ├── AuditTrail.java              # Ordered list of steps
│   ├── DisputeStatus.java           # Status enum
│   ├── TransactionData.java         # Transaction details
│   ├── FraudSignals.java            # Fraud detection results
│   └── MerchantContext.java         # Merchant/delivery data
├── agent/
│   ├── DisputeOrchestratorAgent.java    # Master coordinator
│   ├── FraudDetectionAgent.java         # AI fraud detection
│   ├── TransactionDataAgent.java        # Mock transaction data
│   ├── MerchantContextAgent.java        # Mock merchant data
│   └── ComplianceAgent.java             # AI compliance check
├── service/
│   ├── LlmService.java                  # LLM service interface
│   ├── ClaudeApiService.java            # Anthropic API implementation
│   ├── OllamaApiService.java            # Ollama API implementation
│   └── DisputeService.java              # Business logic
├── controller/
│   ├── DisputeController.java           # REST API
│   ├── HumanReviewController.java       # Review queue API
│   └── UiController.java                # Web UI routes
└── store/
    └── InMemoryDisputeStore.java        # In-memory storage

src/main/resources/
├── application.properties
└── templates/
    ├── index.html                   # Dispute submission form
    ├── result.html                  # Audit trail & decision
    └── review-queue.html            # Human reviewer dashboard
```

## 🧪 Testing

### Manual Testing

1. **Test Fraud Auto-Resolution:**
   - Submit Scenario 1
   - Verify AUTO_RESOLVED status
   - Check audit trail shows fraud detection

2. **Test Merchant Escalation:**
   - Submit Scenario 2
   - Verify ESCALATED_TO_HUMAN status
   - Navigate to review queue
   - Submit human decision
   - Verify HUMAN_RESOLVED status

### Expected Behavior

**Scenario 1 (Fraud):**
- Fraud score: 92/100
- Signals: foreign_location, no_travel_history, unusual_amount
- Decision: AUTO_APPROVED
- Status: AUTO_RESOLVED

**Scenario 2 (Merchant):**
- Delivery status: NOT_DELIVERED
- Merchant disputes: 2 previous
- Decision: ESCALATED
- Status: ESCALATED_TO_HUMAN

## 🔍 Troubleshooting

### API Key Not Set (Anthropic)
**Error:** "API key not configured"
**Solution:** Set `ANTHROPIC_API_KEY` environment variable

### Ollama Connection Failed
**Error:** "Ollama response unavailable"
**Solution:** 
- Ensure Ollama is installed and running
- Check Ollama is accessible at http://localhost:11434
- Verify the model is pulled: `ollama list`

### Port Already in Use
**Error:** "Port 8080 is already in use"
**Solution:** Change port in `application.properties`:
```properties
server.port=8081
```

### LLM API Errors
**Error:** "Agent response unavailable"
**Solution:** 
- **For Anthropic:** Check API key is valid, verify internet connection
- **For Ollama:** Ensure Ollama service is running, check model is available

## 📝 Notes

- **Mock Data:** Transaction and merchant data are hardcoded for demo purposes
- **In-Memory Storage:** Data is lost on application restart
- **No Authentication:** This is a demo application without security
- **AI Responses:** May vary based on Claude's responses

## 🎓 Learning Points

This demo showcases:
1. **Multi-Agent Orchestration**: How multiple AI agents work together
2. **ReAct Pattern**: Transparent AI reasoning process
3. **Human-in-the-Loop**: When and how to escalate to humans
4. **Explainability**: Making AI decisions understandable
5. **Audit Trails**: Complete logging for compliance
6. **Spring Boot Integration**: Building AI systems with Spring

## 📄 License

This is a demonstration project for educational purposes.

## 🤝 Contributing

This is a demo application. Feel free to fork and modify for your learning purposes.

## 📧 Support

For issues or questions about the implementation, refer to the specification document:
`src/main/resources/UC4_Dispute_Management_Implementation_Spec.md`
