# 🎯 Demo Scenarios for RAG-Enhanced Dispute Management System

## Prerequisites
- Application running on http://localhost:8080
- LLM provider configured (Anthropic Claude or Ollama)
- **RAG System Active**: 42 documents loaded (6 policies, 8 regulations, 10 cases, 10 patterns, 8 merchants)

---

## 🆕 **RAG-Enhanced Features to Demonstrate**

### What's New with RAG:
- ✨ **Citation-Based Reasoning**: Every decision cites specific cases, patterns, or policies
- 📊 **Historical Case Matching**: System finds similar past disputes
- 🔍 **Fraud Pattern Recognition**: Matches against 10 known fraud signatures
- 📋 **Policy Compliance**: Validates against 6 banking policies
- 🏪 **Merchant Reputation**: Accesses 8 merchant profiles with history

---

## 📋 **Scenario 1: RAG-Enhanced Fraud Detection - Auto-Resolve**

### Test Case: Foreign Transaction Fraud (Similar to Historical Cases)

**Steps:**
1. Navigate to http://localhost:8080
2. Click **"Load Scenario 1"** or fill manually:
   - **Transaction ID**: `TXN-2026-FR-001`
   - **Merchant Name**: `Foreign Merchant Malaysia`
   - **Dispute Type**: `FRAUD`
   - **Amount**: `25000.00`
   - **Description**: `Unauthorized transaction in foreign country`
   - **Customer Note**: `I did not make this transaction. I was at home in Mumbai.`

**Expected RAG-Enhanced Behavior:**
- ✅ **Status**: `AUTO_RESOLVED`
- ✅ **Decision**: `AUTO_APPROVED` (refund approved)
- ✅ **Fraud Score**: 92/100 (High)

**🔍 RAG Citations to Verify:**
- 📊 **Similar Cases Retrieved**:
  - `CASE-2025-001234`: ₹23,000 foreign transaction → AUTO_APPROVED
  - `CASE-2025-000987`: ₹27,500 Thailand transaction → AUTO_APPROVED
  - `CASE-2024-005432`: ₹22,000 Singapore transaction → AUTO_APPROVED
- 🔍 **Fraud Pattern Matched**:
  - `FP-2025-042`: "Foreign Transaction Fraud" (94.2% accuracy)
- 📋 **Policy Applied**:
  - `POLICY-4.2.1`: "Auto-approve if fraud score ≥ 80/100"

**Agent Steps with RAG:**
1. **Orchestrator**: Analyzes dispute type → Routes to FraudDetectionAgent
2. **FraudDetectionAgent + RAG**:
   - Retrieves 3 similar historical cases
   - Matches fraud pattern FP-2025-042
   - Retrieves relevant policy POLICY-4.2.1
   - Calculates fraud score: 92/100
3. **ComplianceAgent + RAG**:
   - Validates against RBI-4.2 (dispute timeline)
   - Validates against POLICY-4.2.1 (auto-approval threshold)
4. **Decision**: AUTO_APPROVED with citations

**✅ What to Verify:**
- [ ] Explanation includes "Similar to CASE-2025-001234"
- [ ] Mentions "Fraud pattern FP-2025-042 (94.2% accuracy)"
- [ ] Cites "Policy 4.2.1: Auto-approve threshold"
- [ ] Shows 3 similar historical cases in reasoning
- [ ] Resolution time: 5-8 seconds (RAG adds ~2s for retrieval)

---

## 📋 **Scenario 2: RAG-Enhanced Merchant Dispute - Human Escalation**

### Test Case: Item Not Delivered from Known Problematic Merchant

**Steps:**
1. Navigate to http://localhost:8080
2. Click **"Load Scenario 2"** or fill manually:
   - **Transaction ID**: `TXN-2026-MR-002`
   - **Merchant Name**: `QuickShop India`
   - **Dispute Type**: `MERCHANT`
   - **Amount**: `8000.00`
   - **Description**: `Item not delivered after 2 weeks`
   - **Customer Note**: `Ordered 2 weeks ago. Merchant not responding to my messages.`

**Expected RAG-Enhanced Behavior:**
- ⚠️ **Status**: `ESCALATED_TO_HUMAN` or `AUTO_APPROVED` (based on merchant history)
- ✅ **Fraud Score**: Low (merchant dispute)

**🔍 RAG Citations to Verify:**
- 🏪 **Merchant Profile Retrieved**:
  - `MERCH-QS-001`: QuickShop India
  - Dispute Rate: 5% (MEDIUM risk)
  - 2 prior "not delivered" disputes in Q1 2025
  - On-time delivery: 78%
- 📊 **Similar Merchant Disputes**:
  - `CASE-2025-002341`: QuickShop, not delivered → AUTO_APPROVED (5 seconds)
  - `CASE-2025-001876`: QuickShop, disputed delivery → APPROVED after review
- 📋 **Policy Applied**:
  - `POLICY-5.3.4`: "Auto-approve if merchant has 2+ similar disputes"

**Agent Steps with RAG:**
1. **Orchestrator**: Routes to MerchantContextAgent
2. **MerchantContextAgent + RAG**:
   - Retrieves merchant profile MERCH-QS-001
   - Finds 5% dispute rate, 2 prior similar disputes
   - Retrieves delivery performance data
3. **RAG Service**: Finds similar merchant dispute cases
4. **Decision**: May AUTO_APPROVE based on merchant history OR escalate

**✅ What to Verify:**
- [ ] Explanation mentions "QuickShop India has 5% dispute rate"
- [ ] Cites "2 prior 'not delivered' disputes in Q1 2025"
- [ ] References "Similar case CASE-2025-002341"
- [ ] Mentions "Policy 5.3.4: Auto-approve for merchants with 2+ similar disputes"
- [ ] Shows merchant reputation context

---

## 🆕 **Scenario 3: RAG Pattern Matching - Card Cloning Detection**

### Test Case: Multiple Rapid Transactions (Matches Known Pattern)

**Steps:**
1. Fill in the form:
   - **Transaction ID**: `TXN-2026-FR-003`
   - **Merchant Name**: `Multiple Merchants`
   - **Dispute Type**: `FRAUD`
   - **Amount**: `18500.00`
   - **Description**: `Multiple unauthorized transactions in 30 minutes`
   - **Customer Note**: `Card cloned. 5 transactions I didn't make in rapid succession.`

**Expected RAG-Enhanced Behavior:**
- ✅ **Status**: `AUTO_RESOLVED`
- ✅ **Decision**: `AUTO_APPROVED`
- ✅ **Fraud Score**: 98/100 (CRITICAL)

**🔍 RAG Citations to Verify:**
- 🔍 **Fraud Pattern Matched**:
  - `FP-2025-038`: "Card Cloning Pattern" (97.8% accuracy)
  - Indicators: Multiple transactions <1 hour, different locations
- 📊 **Similar Case**:
  - `CASE-2025-004123`: London, multiple transactions → AUTO_APPROVED (98% score)
- 📋 **Policy**:
  - `POLICY-4.2.2`: "Multiple transactions in <1 hour: +20 points"

**✅ What to Verify:**
- [ ] Mentions "Card Cloning Pattern FP-2025-038 (97.8% accuracy)"
- [ ] Cites "Similar to CASE-2025-004123"
- [ ] Shows "CRITICAL risk level"
- [ ] Immediate auto-approval due to high confidence

---

## 🆕 **Scenario 4: RAG Compliance Validation - Regulation Citations**

### Test Case: High-Value Dispute with Regulatory Requirements

**Steps:**
1. Fill in the form:
   - **Transaction ID**: `TXN-2026-FR-004`
   - **Merchant Name**: `Dubai Gold Souk`
   - **Dispute Type**: `FRAUD`
   - **Amount**: `45000.00`
   - **Description**: `Large unauthorized jewelry purchase`
   - **Customer Note**: `I never bought jewelry. This is fraud.`

**Expected RAG-Enhanced Behavior:**
- ✅ **Status**: `AUTO_RESOLVED`
- ✅ **Decision**: `AUTO_APPROVED`
- ✅ **Fraud Score**: 85/100

**🔍 RAG Citations to Verify:**
- 📜 **Regulations Applied**:
  - `RBI-5.1`: "Provisional credit within 10 business days"
  - `RBI-6.3`: "Zero liability for customer if fraud proven"
  - `CPA-12`: "Right to appeal within 15 days"
- 📊 **Similar Case**:
  - `CASE-2025-003456`: Dubai, ₹45,000 → AUTO_APPROVED (85% score)
- 📋 **Policy**:
  - `POLICY-4.2.1`: "High-value transactions require immediate action"

**✅ What to Verify:**
- [ ] Compliance section cites "RBI-5.1", "RBI-6.3", "CPA-12"
- [ ] Mentions "Complies with RBI guidelines"
- [ ] Shows regulatory timeline requirements
- [ ] Audit trail includes regulation references

---

## 🆕 **Scenario 5: RAG Merchant Reputation - High-Risk Merchant**

### Test Case: Dispute with Known Problematic Merchant

**Steps:**
1. Fill in the form:
   - **Transaction ID**: `TXN-2026-MR-005`
   - **Merchant Name**: `BudgetDeals Store`
   - **Dispute Type**: `MERCHANT`
   - **Amount**: `5000.00`
   - **Description**: `Item not as described, poor quality`
   - **Customer Note**: `Product is counterfeit. Merchant won't respond.`

**Expected RAG-Enhanced Behavior:**
- ✅ **Status**: `AUTO_RESOLVED` (fast-track for high-risk merchant)
- ✅ **Decision**: `AUTO_APPROVED`

**🔍 RAG Citations to Verify:**
- 🏪 **Merchant Profile**:
  - `MERCH-BD-005`: BudgetDeals Store
  - Dispute Rate: 8% (HIGH RISK - exceeds 5% threshold)
  - Risk Level: HIGH
  - 4 prior disputes in Q1 2025
- 📋 **Policy**:
  - `POLICY-5.3.4`: "High-risk merchants (>5% dispute rate): Fast-track customer claims"
- 📊 **Similar Cases**: Multiple disputes against this merchant

**✅ What to Verify:**
- [ ] Mentions "BudgetDeals Store has 8% dispute rate (HIGH RISK)"
- [ ] Cites "Policy 5.3.4: Fast-track for high-risk merchants"
- [ ] Shows "Under review for potential suspension"
- [ ] Auto-approves due to merchant's poor reputation

---

## 🆕 **Scenario 6: RAG Premium Merchant - Requires Evidence**

### Test Case: Dispute with Low-Risk Premium Merchant

**Steps:**
1. Fill in the form:
   - **Transaction ID**: `TXN-2026-MR-006`
   - **Merchant Name**: `LuxuryFashion Boutique`
   - **Dispute Type**: `MERCHANT`
   - **Amount**: `15000.00`
   - **Description**: `Size mismatch on luxury item`
   - **Customer Note**: `Ordered size M, received size S.`

**Expected RAG-Enhanced Behavior:**
- ⚠️ **Status**: `ESCALATED_TO_HUMAN`
- ✅ **Reason**: Premium merchant with excellent reputation

**🔍 RAG Citations to Verify:**
- 🏪 **Merchant Profile**:
  - `MERCH-LF-004`: LuxuryFashion Boutique
  - Dispute Rate: 1% (LOW RISK - premium merchant)
  - On-time delivery: 96%
  - Customer rating: 4.7/5
- 📋 **Policy**:
  - `POLICY-5.3.4`: "Low-risk merchants (<1%): Require strong evidence from customer"
- 📜 **Regulation**:
  - `RBI-8.2`: "Merchant has 14 days to provide evidence"

**✅ What to Verify:**
- [ ] Mentions "Premium merchant with 1% dispute rate"
- [ ] Cites "Policy 5.3.4: Extended investigation for low-risk merchants"
- [ ] Escalates to human for balanced review
- [ ] Shows merchant's excellent track record

---

## 🧪 **Additional RAG Test Scenarios**

### Scenario 7: Phishing Attack Pattern
```
Transaction ID: TXN-2026-FR-007
Merchant: NYC Electronics Hub
Dispute Type: FRAUD
Amount: 32000.00
Description: Phishing scam transaction
Customer Note: Received fake email, shared OTP by mistake
```
**Expected RAG**: Matches `FP-2025-051` (Phishing Pattern, 91.5% accuracy)

---

### Scenario 8: Subscription Fraud
```
Transaction ID: TXN-2026-FR-008
Merchant: Foreign Subscription Service
Dispute Type: FRAUD
Amount: 2500.00
Description: Recurring charges I never authorized
Customer Note: Small charges adding up, never signed up
```
**Expected RAG**: Matches `FP-2025-106` (Subscription Fraud, 75.3% accuracy)

---

### Scenario 9: Reliable Merchant Dispute
```
Transaction ID: TXN-2026-MR-009
Merchant: TechGadgets Pro
Dispute Type: MERCHANT
Amount: 12000.00
Description: Defective product
Customer Note: Product stopped working after 1 week
```
**Expected RAG**: Retrieves `MERCH-TG-002` (2% dispute rate, LOW RISK) → Escalate for evidence

---

### Scenario 10: Healthcare Merchant (Critical Category)
```
Transaction ID: TXN-2026-MR-010
Merchant: HealthEssentials Pharmacy
Dispute Type: MERCHANT
Amount: 3000.00
Description: Wrong medicine delivered
Customer Note: Ordered Medicine A, received Medicine B
```
**Expected RAG**: Retrieves `MERCH-HE-006` (1% dispute rate, healthcare category) → Immediate escalation

---

## 🔍 **RAG-Specific Verification Checklist**

### ✅ Knowledge Base Access
- [ ] System loads 42 documents on startup
- [ ] Startup logs show: "Loaded 6 policies, 8 regulations, 10 cases, 10 patterns, 8 merchants"
- [ ] Vector store statistics displayed: `{totalDocuments=42, categoryCounts=...}`

### ✅ Citation Quality
- [ ] Every decision includes specific document IDs (e.g., CASE-2025-001234)
- [ ] Fraud patterns cited with accuracy percentages
- [ ] Policies cited with section numbers
- [ ] Regulations cited with authority (RBI, CPA, IT Act)
- [ ] Merchant profiles cited with dispute rates

### ✅ Semantic Search Accuracy
- [ ] Similar cases are actually relevant (check descriptions)
- [ ] Fraud patterns match the dispute characteristics
- [ ] Policies retrieved are applicable to dispute type
- [ ] Merchant data is for the correct merchant

### ✅ RAG Performance
- [ ] Retrieval adds 2-3 seconds to processing time
- [ ] No errors in embedding generation
- [ ] Vector search returns results (not empty)
- [ ] Similarity scores are reasonable (>0.4 for relevant docs)

### ✅ Enhanced Explanations
- [ ] Explanations are more detailed than before
- [ ] Include "Based on similar case..." phrases
- [ ] Reference specific fraud patterns by ID
- [ ] Cite policy sections and regulation numbers
- [ ] Mention merchant reputation explicitly

---

## 📊 **Expected Response Times (with RAG)**

| Scenario | Without RAG | With RAG | RAG Overhead |
|----------|-------------|----------|--------------|
| Auto-resolve (fraud) | 3-5s | 5-8s | +2-3s |
| Auto-reject (fraud) | 3-5s | 5-8s | +2-3s |
| Escalate (merchant) | 2-4s | 4-6s | +2s |
| Human review | Manual | Manual | N/A |

**Note**: RAG overhead is for:
- Embedding generation (query)
- Vector search (3-5 documents)
- Context injection into prompt

---

## 🎭 **RAG System Health Checks**

### Verify RAG is Working
```bash
# Check startup logs
tail -f logs/application.log | grep "RAG"

# Expected output:
# INFO: Starting RAG data loading...
# INFO: Loaded 6 policies
# INFO: Loaded 8 regulations
# INFO: Loaded 10 historical cases
# INFO: Loaded 10 fraud patterns
# INFO: Loaded 8 merchants
# INFO: RAG data loading completed successfully!
```

### Test RAG Retrieval
1. Submit any fraud dispute
2. Check agent reasoning in result page
3. Look for phrases like:
   - "Similar to CASE-..."
   - "Matches fraud pattern FP-..."
   - "According to policy POLICY-..."
   - "Complies with regulation RBI-..."

### Verify Vector Store
- Check logs for: `{totalDocuments=42, categoryCounts={POLICY=6, REGULATION=8, FRAUD_PATTERN=10, CASE=10, MERCHANT=8}}`
- If count is wrong, check JSON files in `src/main/resources/rag-data/`

---

## 🐛 **RAG-Specific Troubleshooting**

### No Citations in Decisions
**Problem**: Decisions don't include document IDs
**Solutions**:
- Check RAG data loaded: Look for "RAG data loading completed" in logs
- Verify vector store size: Should be 42 documents
- Check agent prompts include RAG context
- Ensure RagService is injected into agents

### Irrelevant Documents Retrieved
**Problem**: Retrieved cases/patterns don't match dispute
**Solutions**:
- Check similarity threshold (default: 0.4-0.5)
- Verify embedding model is working
- Review query construction in RagService
- Check document content in JSON files

### Slow Performance
**Problem**: RAG adds >5 seconds to processing
**Solutions**:
- Check embedding model initialization (should be once at startup)
- Monitor vector search performance
- Consider reducing topK parameter (default: 3-5)
- Verify in-memory vector store is used (not disk-based)

### Missing Knowledge Base
**Problem**: "No documents found" errors
**Solutions**:
- Verify JSON files exist in `src/main/resources/rag-data/`
- Check file permissions
- Review RagDataLoader logs for errors
- Ensure JSON format is valid

---

## 📝 **RAG-Enhanced Test Checklist**

Use this checklist to verify RAG functionality:

- [ ] **Startup**: 42 documents loaded successfully
- [ ] **Scenario 1**: Fraud with case citations (CASE-2025-001234, etc.)
- [ ] **Scenario 2**: Merchant with profile data (MERCH-QS-001)
- [ ] **Scenario 3**: Pattern matching (FP-2025-038)
- [ ] **Scenario 4**: Regulation citations (RBI-5.1, CPA-12)
- [ ] **Scenario 5**: High-risk merchant fast-track (MERCH-BD-005)
- [ ] **Scenario 6**: Premium merchant escalation (MERCH-LF-004)
- [ ] **Scenario 7**: Phishing pattern (FP-2025-051)
- [ ] **Scenario 8**: Subscription fraud (FP-2025-106)
- [ ] **Scenario 9**: Reliable merchant (MERCH-TG-002)
- [ ] **Scenario 10**: Healthcare category (MERCH-HE-006)
- [ ] All decisions include specific citations
- [ ] Similar cases are relevant
- [ ] Fraud patterns match characteristics
- [ ] Policies are correctly applied
- [ ] Merchant data is accurate
- [ ] Compliance checks cite regulations
- [ ] Performance is acceptable (<10s)

---

## 🚀 **Quick Start Commands**

```bash
# Start application with RAG
mvn spring-boot:run

# Verify RAG loaded
curl http://localhost:8080/actuator/health
# Or check logs for "RAG data loading completed"

# Access main page
open http://localhost:8080

# Access review queue
open http://localhost:8080/review

# Test fraud scenario with RAG
curl -X POST http://localhost:8080/api/disputes \
  -H "Content-Type: application/json" \
  -d '{
    "transactionId": "TXN-TEST-001",
    "disputeType": "FRAUD",
    "amount": 25000.0,
    "description": "Foreign transaction fraud",
    "customerNote": "I was at home in Mumbai"
  }'
```

---

## 💡 **Tips for RAG Demo**

1. **Start with Scenario 1** - Shows clear case citations
2. **Highlight citations** - Point out specific CASE-IDs, pattern IDs
3. **Show merchant reputation** - Scenario 5 vs Scenario 6 contrast
4. **Demonstrate pattern matching** - Scenario 3 with 97.8% accuracy
5. **Explain compliance** - Scenario 4 with regulation citations
6. **Compare before/after** - Show how RAG improves explanations
7. **Show knowledge base** - Mention 42 documents, 5 categories
8. **Emphasize accuracy** - Fraud patterns with 78-98% accuracy
9. **Highlight learning** - System improves as more data is added
10. **Discuss scalability** - Easy to add more documents

---

## 📚 **RAG Documentation References**

- **RAG Architecture**: See `README.md` - RAG Architecture section
- **Feasibility Study**: See `RAG_FEASIBILITY_STUDY.md`
- **Knowledge Base**: Check `src/main/resources/rag-data/` JSON files
- **RAG Services**: Review `src/main/java/com/demo/dispute/rag/`
- **Agent Integration**: See enhanced agents in `src/main/java/com/demo/dispute/agent/`

---

**Last Updated**: 2026-05-03
**Version**: 2.0.0 (RAG-Enhanced)
**Knowledge Base**: 42 documents (6 policies, 8 regulations, 10 cases, 10 patterns, 8 merchants)