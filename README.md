# DisputeResolver - AI-Powered Dispute Management System

A Spring Boot application demonstrating an **agentic AI system** for automated dispute resolution using the **ReAct pattern** (Reason-Act-Observe). This system showcases multi-agent orchestration, explainable AI decisions, and human-in-the-loop workflows for banking dispute management.

## 🎯 Overview

DisputeResolver is a proof-of-concept application that simulates how AI agents can work together to analyze, process, and resolve customer disputes in a banking context. The system demonstrates:

- **Multi-Agent Architecture**: Specialized agents working together to solve complex problems
- **ReAct Pattern**: Think → Act → Observe cycles for transparent decision-making
- **Explainable AI**: Every decision includes clear reasoning and audit trails
- **Human-in-the-Loop**: Automatic escalation for ambiguous cases requiring human judgment
- **Dual LLM Support**: Works with both Anthropic Claude API and local Ollama models

## 🏗️ Architecture

### Multi-Agent System

The application uses a coordinated multi-agent architecture where each agent has a specific responsibility:

```
┌─────────────────────────────────────────────────────────────┐
│                  DisputeOrchestratorAgent                   │
│              (Master Coordinator - ReAct Loop)              │
└─────────────────────────────────────────────────────────────┘
                            │
        ┌───────────────────┼───────────────────┐
        │                   │                   │
        ▼                   ▼                   ▼
┌──────────────┐    ┌──────────────┐    ┌──────────────┐
│   Fraud      │    │ Transaction  │    │  Merchant    │
│  Detection   │    │     Data     │    │   Context    │
│    Agent     │    │    Agent     │    │    Agent     │
└──────────────┘    └──────────────┘    └──────────────┘
        │                   │                   │
        └───────────────────┼───────────────────┘
                            │
                            ▼
                    ┌──────────────┐
                    │ Compliance   │
                    │    Agent     │
                    └──────────────┘
```

#### Agent Responsibilities

1. **DisputeOrchestratorAgent** (Master)
   - Coordinates the entire dispute resolution workflow
   - Implements the ReAct pattern (Think → Act → Observe)
   - Routes disputes based on type (FRAUD vs MERCHANT)
   - Makes final decisions or escalates to humans
   - Maintains complete audit trail

2. **FraudDetectionAgent**
   - Analyzes transaction patterns for fraud signals
   - Calculates fraud risk scores (0-100)
   - Identifies anomalies (location, amount, timing)
   - Provides recommendations for auto-approval

3. **TransactionDataAgent**
   - Fetches transaction details (mock data for demo)
   - Provides context: amount, location, merchant, timestamp
   - Simulates integration with payment systems

4. **MerchantContextAgent**
   - Retrieves merchant history and reputation
   - Checks delivery status and tracking
   - Analyzes past dispute patterns
   - Assesses merchant risk level

5. **ComplianceAgent**
   - Validates decisions against banking policies
   - Ensures regulatory compliance
   - Reviews audit trail completeness
   - Flags potential compliance issues

### ReAct Pattern Implementation

Each dispute follows a structured reasoning cycle:

```
THINK → ACT → OBSERVE → THINK → ACT → OBSERVE → DECISION
```

**Example Flow:**

1. **THINK**: "This is a fraud dispute. I need transaction data and fraud signals."
2. **ACT**: Call TransactionDataAgent → Get transaction details
3. **OBSERVE**: "Transaction in unknown foreign country, no travel history"
4. **THINK**: "High fraud risk. Need fraud analysis."
5. **ACT**: Call FraudDetectionAgent → Get fraud score
6. **OBSERVE**: "Fraud score: 92/100. Strong signals detected."
7. **THINK**: "Score exceeds threshold. Safe to auto-approve."
8. **ACT**: Call ComplianceAgent → Validate decision
9. **OBSERVE**: "Decision compliant with policy."
10. **DECISION**: Generate final decision with all context
11. **FINAL DECISION**:
    ```
    ┌─────────────────────────────────────────────────────────────┐
    │                    FINAL DECISION                           │
    ├─────────────────────────────────────────────────────────────┤
    │  Decision: AUTO_APPROVED                                    │
    │  Confidence Score: 92/100 (HIGH)                            │
    │                                                             │
    │  📋 Policies Applied:                                       │
    │  • POLICY-4.2.1: Auto-approve if fraud score ≥ 80/100      │
    │                                                             │
    │  📊 Similar Past Cases:                                     │
    │  • CASE-2025-001234: ₹23,000 foreign → AUTO_APPROVED       │
    │  • CASE-2025-000987: ₹27,500 Thailand → AUTO_APPROVED      │
    │  • CASE-2025-005432: ₹22,000 Singapore → AUTO_APPROVED     │
    │                                                             │
    │  🔍 Fraud Pattern Matched:                                  │
    │  • FP-2025-042: Foreign Transaction Fraud (94.2% accuracy) │
    └─────────────────────────────────────────────────────────────┘
    ```

## 🚀 Features

### 1. RAG-Enhanced Decision Making (NEW!)
- **Retrieval-Augmented Generation**: Agents access historical knowledge for better decisions
- **42 Knowledge Documents**: Policies, regulations, cases, fraud patterns, and merchant data
- **Semantic Search**: Find relevant information using AI embeddings
- **Citation-Based Reasoning**: Every decision cites specific cases, patterns, or policies
- **Continuous Learning**: System improves as more data is added

**Knowledge Bases:**
- 📋 **6 Banking Policies**: Auto-approval thresholds, fraud scoring, merchant dispute rules
- 📜 **8 Regulations**: RBI guidelines, consumer protection laws, compliance requirements
- 📊 **10 Historical Cases**: Past dispute resolutions with outcomes and reasoning
- 🔍 **10 Fraud Patterns**: Known fraud signatures with accuracy metrics
- 🏪 **8 Merchant Profiles**: Reputation, dispute rates, delivery performance

### 2. Automated Dispute Resolution
- **Fraud Detection**: Automatically identifies and approves legitimate fraud claims
- **Pattern Recognition**: Analyzes transaction patterns, locations, and customer behavior
- **Risk Scoring**: Calculates fraud probability scores
- **Auto-Approval**: Resolves clear-cut cases without human intervention

### 2. Human-in-the-Loop
- **Smart Escalation**: Routes ambiguous cases to human reviewers
- **Review Queue**: Dashboard for human reviewers to process escalated cases
- **Decision Support**: Provides AI recommendations to assist human judgment
- **Audit Trail**: Complete history of AI reasoning for review

### 3. Explainable AI
- **Transparent Reasoning**: Every decision includes detailed explanation
- **Step-by-Step Audit**: Visual timeline of agent interactions
- **Confidence Levels**: HIGH/MEDIUM/LOW confidence indicators
- **Compliance Notes**: Regulatory compliance validation

### 4. Dual LLM Support
- **Anthropic Claude**: Production-grade API integration
- **Ollama**: Local LLM support for development/testing
- **Easy Switching**: Toggle between providers via configuration

### 5. Vector Database & Embeddings
- **In-Memory Vector Store**: Fast semantic search using cosine similarity
- **Local Embeddings**: all-MiniLM-L6-v2 model (384 dimensions, no API needed)
- **Automatic Data Loading**: Knowledge base loaded on application startup
- **42 Documents Indexed**: Policies, regulations, cases, patterns, merchants

## 🧠 RAG Architecture

The system uses Retrieval-Augmented Generation to enhance AI decision-making:

```
┌─────────────────────────────────────────────────────────────┐
│                    RAG-Enhanced System                      │
└─────────────────────────────────────────────────────────────┘
                            │
        ┌───────────────────┼───────────────────┐
        │                   │                   │
        ▼                   ▼                   ▼
┌──────────────┐    ┌──────────────┐    ┌──────────────┐
│   Policies   │    │ Regulations  │    │   Cases      │
│  (6 docs)    │    │  (8 docs)    │    │  (10 docs)   │
└──────────────┘    └──────────────┘    └──────────────┘
        │                   │                   │
        └───────────────────┼───────────────────┘
                            │
                            ▼
                ┌───────────────────────┐
                │  Embedding Service    │
                │  (all-MiniLM-L6-v2)   │
                └───────────────────────┘
                            │
                            ▼
                ┌───────────────────────┐
                │  Vector Store         │
                │  (In-Memory, 42 docs) │
                └───────────────────────┘
                            │
        ┌───────────────────┼───────────────────┐
        │                   │                   │
        ▼                   ▼                   ▼
┌──────────────┐    ┌──────────────┐    ┌──────────────┐
│    Fraud     │    │  Merchant    │    │ Compliance   │
│  Detection   │    │   Context    │    │    Agent     │
│  Agent+RAG   │    │  Agent+RAG   │    │   +RAG       │
└──────────────┘    └──────────────┘    └──────────────┘
```

**How RAG Works:**

1. **Data Ingestion**: On startup, load 42 documents from JSON files
2. **Embedding Generation**: Convert each document to 384-dim vector using local model
3. **Vector Storage**: Store embeddings in in-memory vector database
4. **Query Processing**: When agent needs context, generate query embedding
5. **Semantic Search**: Find top-K similar documents using cosine similarity
6. **Context Enhancement**: Inject retrieved documents into LLM prompt
7. **Citation**: LLM response includes specific document IDs as citations

**Example RAG Flow:**

```
User: "Dispute for ₹25,000 foreign transaction"
    ↓
FraudDetectionAgent: "Need fraud analysis"
    ↓
RAG Service: Search for similar cases
    ↓
Vector Store: Returns 3 similar cases (CASE-2025-001234, etc.)
    ↓
RAG Service: Search for fraud patterns
    ↓
Vector Store: Returns pattern FP-2025-042 (94.2% accuracy)
    ↓
RAG Service: Search for policies
    ↓
Vector Store: Returns POLICY-4.2.1 (auto-approval threshold)
    ↓
Enhanced Prompt: "Based on CASE-2025-001234, FP-2025-042, POLICY-4.2.1..."
    ↓
LLM: "Auto-approve. Similar to CASE-2025-001234 (92% fraud score)..."
    ↓
┌─────────────────────────────────────────────────────────────┐
│                    FINAL DECISION                           │
├─────────────────────────────────────────────────────────────┤
│  Decision: AUTO_APPROVED                                    │
│  Confidence Score: 92/100 (HIGH)                            │
│                                                             │
│  📋 Policies Applied:                                       │
│  • POLICY-4.2.1: Auto-approve if fraud score ≥ 80/100      │
│                                                             │
│  📊 Similar Past Cases:                                     │
│  • CASE-2025-001234: ₹23,000 foreign → AUTO_APPROVED       │
│  • CASE-2025-000987: ₹27,500 Thailand → AUTO_APPROVED      │
│  • CASE-2025-005432: ₹22,000 Singapore → AUTO_APPROVED     │
│                                                             │
│  🔍 Fraud Pattern Matched:                                  │
│  • FP-2025-042: Foreign Transaction Fraud (94.2% accuracy) │
└─────────────────────────────────────────────────────────────┘
```

## 📋 Prerequisites

- **Java**: JDK 17 or higher
- **Maven**: 3.6+ (or use system Maven)
- **Git**: For version control
- **LLM Access**: Either Anthropic API key OR Ollama installed locally
- **Memory**: Minimum 2GB RAM for embedding model (runs locally)

### LLM Setup Options

#### Option 1: Anthropic Claude (Recommended for Production)
```bash
export ANTHROPIC_API_KEY=sk-ant-your-api-key-here
```

#### Option 2: Ollama (Local Development)
```bash
# Install Ollama
brew install ollama  # macOS
# or download from https://ollama.ai

# Pull a model
ollama pull gemma2:2b

# Start Ollama server
ollama serve
```

## 🛠️ Installation & Setup

### 1. Clone the Repository
```bash
git clone https://github.com/pravinw22/DisputeResolver.git
cd DisputeResolver
```

### 2. Configure LLM Provider

Edit `src/main/resources/application.properties`:

**For Anthropic Claude:**
```properties
llm.provider=anthropic
anthropic.api.key=${ANTHROPIC_API_KEY}
anthropic.model=claude-sonnet-4-20250514
```

**For Ollama (Local):**
```properties
llm.provider=ollama
ollama.api.url=http://localhost:11434/api/generate
ollama.model=gemma2:2b
```

### 3. Build the Project
```bash
# Using Maven wrapper (recommended)
./mvnw clean install

# Or using system Maven
mvn clean install
```

### 4. Run the Application
```bash
# Using system Maven
mvn spring-boot:run

# Or run the JAR directly
java -jar target/dispute-management-demo-1.0.0.jar
```

**On Startup, RAG System Will:**
- Load 6 banking policies
- Load 8 regulations (RBI, Consumer Protection Act, IT Act)
- Load 10 historical dispute cases
- Load 10 fraud patterns
- Load 8 merchant profiles
- Generate embeddings for all 42 documents
- Store in in-memory vector database

**Expected Startup Logs:**
```
INFO com.demo.dispute.rag.RagDataLoader : Starting RAG data loading...
INFO com.demo.dispute.rag.RagDataLoader : Loading policies...
INFO com.demo.dispute.rag.RagDataLoader : Loaded 6 policies
INFO com.demo.dispute.rag.RagDataLoader : Loading regulations...
INFO com.demo.dispute.rag.RagDataLoader : Loaded 8 regulations
INFO com.demo.dispute.rag.RagDataLoader : Loading historical cases...
INFO com.demo.dispute.rag.RagDataLoader : Loaded 10 historical cases
INFO com.demo.dispute.rag.RagDataLoader : Loading fraud patterns...
INFO com.demo.dispute.rag.RagDataLoader : Loaded 10 fraud patterns
INFO com.demo.dispute.rag.RagDataLoader : Loading merchant data...
INFO com.demo.dispute.rag.RagDataLoader : Loaded 8 merchants
INFO com.demo.dispute.rag.RagDataLoader : RAG data loading completed successfully!
INFO com.demo.dispute.rag.RagDataLoader : {totalDocuments=42, categoryCounts={POLICY=6, REGULATION=8, FRAUD_PATTERN=10, CASE=10, MERCHANT=8}}
```

### 5. Access the Application
Open your browser and navigate to:
```
http://localhost:8080
```

## 📖 Usage Guide

### Demo Scenarios

The application includes two pre-configured scenarios:

#### Scenario 1: Fraudulent Transaction (Auto-Resolution)
**Use Case**: Customer reports unauthorized transaction in foreign country

**Input:**
- Transaction: ₹25,000 debit card charge
- Location: Unknown foreign country
- Customer: No travel history, was at home in Mumbai

**Expected Flow:**
1. System fetches transaction data
2. Fraud detection analyzes signals
3. High fraud score (92/100) detected
4. Auto-approves dispute
5. Compliance validates decision

**Result**: ✅ AUTO_RESOLVED

#### Scenario 2: Merchant Dispute (Human-in-Loop)
**Use Case**: Customer claims item not delivered

**Input:**
- Transaction: ₹8,000 e-commerce charge
- Merchant: QuickShop India
- Issue: Item not delivered after 2 weeks
- Merchant: Not responding to customer

**Expected Flow:**
1. System fetches transaction and merchant data
2. Checks delivery status: NOT_DELIVERED
3. Reviews merchant history: 2 prior disputes
4. Cannot auto-resolve due to ambiguity
5. Escalates to human review queue

**Result**: 🔄 ESCALATED_TO_HUMAN

### Step-by-Step Walkthrough

#### 1. Submit a Dispute
1. Go to http://localhost:8080
2. Click "Load Scenario 1" or "Load Scenario 2"
3. Review pre-filled form data
4. Click "Submit Dispute"

#### 2. View Results
- **Auto-Resolved Cases**: See green success banner with decision
- **Escalated Cases**: See orange warning banner
- **Audit Trail**: Detailed step-by-step agent interactions
- **Agent Flow**: Visual diagram of agent participation

#### 3. Human Review (for Escalated Cases)
1. Click "View Review Queue" or go to http://localhost:8080/review
2. See all escalated cases
3. Click "View Full Audit Trail" to review AI reasoning
4. Click "✅ Approve" or "❌ Reject"
5. Add reviewer note explaining decision
6. Submit decision

## 🔌 API Documentation

### REST Endpoints

#### Submit Dispute
```http
POST /api/disputes
Content-Type: application/json

{
  "transactionId": "TXN-123456",
  "disputeType": "FRAUD",
  "amount": 25000.0,
  "description": "Unauthorized transaction in foreign country",
  "customerNote": "I did not make this transaction",
  "merchantName": "Optional for MERCHANT disputes"
}
```

**Response:**
```json
{
  "caseId": "ABC123",
  "status": "AUTO_RESOLVED",
  "finalDecision": "AUTO_APPROVED",
  "explanation": "Fraud score 92/100. Strong fraud signals detected.",
  "auditTrail": {
    "steps": [...]
  },
  "createdAt": "2026-04-24T15:30:00",
  "resolvedAt": "2026-04-24T15:30:05"
}
```

#### Get Dispute Details
```http
GET /api/disputes/{caseId}
```

#### List All Disputes
```http
GET /api/disputes
```

#### Get Review Queue
```http
GET /api/review/queue
```

#### Submit Human Decision
```http
POST /api/review/{caseId}/decision
Content-Type: application/json

{
  "decision": "APPROVED",
  "note": "Verified with merchant. Customer claim is valid."
}
```

### UI Routes

- `GET /` - Home page (dispute submission form)
- `GET /result?caseId={id}` - View dispute result and audit trail
- `GET /review` - Human review queue dashboard

## 🗂️ Project Structure

```
DisputeResolver/
├── src/
│   ├── main/
│   │   ├── java/com/demo/dispute/
│   │   │   ├── DisputeApplication.java          # Main Spring Boot app
│   │   │   ├── agent/                           # AI Agents (RAG-Enhanced)
│   │   │   │   ├── DisputeOrchestratorAgent.java
│   │   │   │   ├── FraudDetectionAgent.java     # ✨ RAG-enhanced
│   │   │   │   ├── TransactionDataAgent.java
│   │   │   │   ├── MerchantContextAgent.java    # ✨ RAG-enhanced
│   │   │   │   └── ComplianceAgent.java         # ✨ RAG-enhanced
│   │   │   ├── config/                          # Configuration
│   │   │   │   ├── AnthropicConfig.java
│   │   │   │   └── Prompts.java
│   │   │   ├── controller/                      # REST & UI Controllers
│   │   │   │   ├── DisputeController.java
│   │   │   │   ├── HumanReviewController.java
│   │   │   │   └── UiController.java
│   │   │   ├── model/                           # Domain Models
│   │   │   │   ├── DisputeCase.java
│   │   │   │   ├── DisputeRequest.java
│   │   │   │   ├── AgentStep.java
│   │   │   │   ├── AuditTrail.java
│   │   │   │   ├── DisputeStatus.java
│   │   │   │   ├── FraudSignals.java
│   │   │   │   ├── MerchantContext.java
│   │   │   │   └── TransactionData.java
│   │   │   ├── rag/                             # ✨ RAG System (NEW!)
│   │   │   │   ├── EmbeddingService.java        # Local embedding model
│   │   │   │   ├── InMemoryVectorStore.java     # Vector database
│   │   │   │   ├── RagService.java              # RAG orchestration
│   │   │   │   ├── RagDataLoader.java           # Data ingestion
│   │   │   │   ├── RagDocument.java             # Document model
│   │   │   │   └── RagSearchResult.java         # Search result model
│   │   │   ├── service/                         # Business Logic
│   │   │   │   ├── DisputeService.java
│   │   │   │   ├── ClaudeApiService.java
│   │   │   │   ├── OllamaApiService.java
│   │   │   │   └── LlmService.java
│   │   │   └── store/                           # Data Storage
│   │   │       └── InMemoryDisputeStore.java
│   │   └── resources/
│   │       ├── application.properties           # App configuration
│   │       ├── rag-data/                        # ✨ RAG Knowledge Base (NEW!)
│   │       │   ├── policies.json                # 6 banking policies
│   │       │   ├── regulations.json             # 8 regulations
│   │       │   ├── historical-cases.json        # 10 past cases
│   │       │   ├── fraud-patterns.json          # 10 fraud patterns
│   │       │   └── merchants.json               # 8 merchant profiles
│   │       ├── templates/                       # Thymeleaf templates
│   │       │   ├── index.html
│   │       │   ├── result.html
│   │       │   └── review-queue.html
│   │       ├── UC4_Dispute_Management_Implementation_Spec.md
│   │       └── DEMO_SCENARIOS.md
│   └── test/
├── pom.xml                                      # Maven dependencies (+ LangChain4j)
├── .gitignore
├── README.md
└── RAG_FEASIBILITY_STUDY.md                     # ✨ RAG implementation guide
```

## 🔧 Configuration

### Application Properties

```properties
# LLM Provider Selection
llm.provider=ollama                              # or "anthropic"

# Anthropic Configuration
anthropic.api.key=${ANTHROPIC_API_KEY}
anthropic.api.url=https://api.anthropic.com/v1/messages
anthropic.model=claude-sonnet-4-20250514

# Ollama Configuration
ollama.api.url=http://localhost:11434/api/generate
ollama.model=gemma2:2b

# Server Configuration
server.port=8080
spring.application.name=dispute-management-demo

# Thymeleaf Configuration
spring.thymeleaf.cache=false
spring.thymeleaf.prefix=classpath:/templates/
spring.thymeleaf.suffix=.html

# Logging
logging.level.com.demo.dispute=INFO
```

## 🧪 Testing

### Manual Testing

1. **Test Fraud Detection:**
   - Load Scenario 1
   - Submit dispute
   - Verify AUTO_RESOLVED status
   - Check audit trail shows fraud analysis

2. **Test Human Escalation:**
   - Load Scenario 2
   - Submit dispute
   - Verify ESCALATED_TO_HUMAN status
   - Go to review queue
   - Approve/Reject the case

3. **Test API Endpoints:**
```bash
# Submit dispute
curl -X POST http://localhost:8080/api/disputes \
  -H "Content-Type: application/json" \
  -d '{
    "transactionId": "TXN-TEST-001",
    "disputeType": "FRAUD",
    "amount": 25000.0,
    "description": "Test fraud case",
    "customerNote": "Testing"
  }'

# Get dispute
curl http://localhost:8080/api/disputes/{caseId}

# Get review queue
curl http://localhost:8080/api/review/queue
```

## 🎨 UI Screenshots

### Home Page
- Dispute submission form
- Pre-configured scenario buttons
- Clean, intuitive interface

### Result Page
- Decision banner (green for approved, orange for escalated)
- Agent interaction flow diagram
- Detailed audit trail table
- Compliance validation notes

### Review Queue
- List of escalated cases
- Case details and AI recommendations
- Approve/Reject buttons
- Reviewer note input

## 🔐 Security Considerations

### Current Implementation (Demo)
- No authentication/authorization
- In-memory storage (data lost on restart)
- Mock data for transactions and merchants
- No encryption for sensitive data

### Production Recommendations
- Implement OAuth2/JWT authentication
- Use persistent database (PostgreSQL, MySQL)
- Encrypt sensitive data at rest and in transit
- Add rate limiting and API throttling
- Implement audit logging to secure storage
- Add input validation and sanitization
- Use HTTPS for all communications
- Implement role-based access control (RBAC)

## 🚧 Limitations & Future Enhancements

### Current Limitations
- In-memory storage (no persistence for disputes and vector store)
- Mock data for transactions
- No real payment gateway integration
- Limited to two dispute types
- No batch processing
- No analytics dashboard
- Local embeddings only (no cloud-based alternatives yet)

### Planned Enhancements
- [ ] Persistent database integration (PostgreSQL + pgvector)
- [ ] Cloud vector database (Pinecone, Weaviate)
- [ ] Real-time fraud detection APIs
- [ ] Advanced analytics and reporting
- [ ] Multi-language support
- [ ] Email notifications
- [ ] Webhook integrations
- [ ] Batch dispute processing
- [ ] Fine-tuned embedding models
- [ ] Performance monitoring
- [ ] A/B testing framework
- [ ] RAG feedback loop for continuous improvement
- [ ] Hybrid search (vector + keyword)

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📝 License

This project is a demonstration/proof-of-concept and is provided as-is for educational purposes.

## 👥 Authors

- **Pravin Walunj** - [pravinw22](https://github.com/pravinw22)

## 🙏 Acknowledgments

- Anthropic for Claude API
- Ollama for local LLM support
- Spring Boot team for the excellent framework
- Thymeleaf for templating engine
- Bootstrap for UI components

## 📞 Support

For questions or issues:
- Open an issue on GitHub
- Check the documentation in `src/main/resources/`
- Review the implementation spec: `UC4_Dispute_Management_Implementation_Spec.md`

## 🔗 Related Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Anthropic Claude API](https://docs.anthropic.com/)
- [Ollama Documentation](https://ollama.ai/docs)
- [ReAct Pattern Paper](https://arxiv.org/abs/2210.03629)
- [Thymeleaf Documentation](https://www.thymeleaf.org/)
- [LangChain4j Documentation](https://docs.langchain4j.dev/)
- [RAG Feasibility Study](RAG_FEASIBILITY_STUDY.md) - Detailed RAG implementation guide

---

**Built with ❤️ using Spring Boot, AI Agents, RAG, and the ReAct Pattern**