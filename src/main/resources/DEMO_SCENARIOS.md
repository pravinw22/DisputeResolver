# 🎯 Demo Scenarios for Dispute Management System

## Prerequisites
- Application running on http://localhost:8080
- LLM provider configured (Anthropic Claude or Ollama)

---

## 📋 **Scenario 1: Fraud Detection - Auto-Resolve**

### Test Case: Suspicious Transaction from Unusual Location

**Steps:**
1. Navigate to http://localhost:8080
2. Fill in the form:
   - **Transaction ID**: `TXN-FRAUD-001`
   - **Merchant Name**: `SuspiciousMerchant`
   - **Dispute Type**: `fraud`
   - **Amount**: `500.00`
   - **Description**: `Unauthorized charge from foreign country`
   - **Customer Note**: `I never made this purchase, card was stolen`

**Expected Behavior:**
- ✅ **Status**: `RESOLVED`
- ✅ **Decision**: `AUTO_APPROVED` (refund approved)
- ✅ **Fraud Score**: High (>70)
- ✅ **Agent Steps Visible**:
  1. Orchestrator analyzes dispute type
  2. FraudDetectionAgent detects high-risk signals
  3. TransactionDataAgent finds unusual location/device
  4. ComplianceAgent validates decision
- ✅ **Explanation**: Clear reasoning about fraud indicators
- ✅ **Resolution Time**: < 5 seconds

---

## 📋 **Scenario 2: Merchant Dispute - Human Escalation**

### Test Case: Item Not Received Dispute

**Steps:**
1. Navigate to http://localhost:8080
2. Fill in the form:
   - **Transaction ID**: `TXN-MERCHANT-002`
   - **Merchant Name**: `OnlineRetailer`
   - **Dispute Type**: `merchant_dispute`
   - **Amount**: `150.00`
   - **Description**: `Item never delivered`
   - **Customer Note**: `Ordered 2 weeks ago, tracking shows not delivered`

**Expected Behavior:**
- ⚠️ **Status**: `PENDING_HUMAN_REVIEW`
- ⚠️ **Decision**: `ESCALATED_TO_HUMAN`
- ✅ **Fraud Score**: Low (<30)
- ✅ **Agent Steps Visible**:
  1. Orchestrator identifies merchant dispute
  2. FraudDetectionAgent finds low fraud risk
  3. MerchantContextAgent retrieves merchant history
  4. Orchestrator escalates due to complexity
- ✅ **Explanation**: Requires human review for merchant evidence
- ✅ **Case appears in Review Queue**: http://localhost:8080/review-queue

**Human Review Steps:**
1. Go to http://localhost:8080/review-queue
2. Click "Review" on the pending case
3. Review all agent reasoning steps
4. Make decision: Approve or Reject
5. Add reviewer notes
6. Submit decision

**After Human Review:**
- ✅ **Status**: `RESOLVED`
- ✅ **Decision**: `APPROVED` or `REJECTED` (based on reviewer)
- ✅ **Reviewer Notes**: Visible in audit trail

---

## 🧪 **Additional Test Scenarios**

### Scenario 3: Low-Risk Fraud (Auto-Reject)
```
Transaction ID: TXN-VALID-003
Merchant: TrustedMerchant
Dispute Type: fraud
Amount: 25.00
Description: Legitimate purchase disputed
Customer Note: I don't recognize this charge
```
**Expected**: Auto-rejected (fraud score <30, legitimate transaction)

---

### Scenario 4: High-Value Merchant Dispute (Escalate)
```
Transaction ID: TXN-HIGH-004
Merchant: PremiumStore
Dispute Type: merchant_dispute
Amount: 5000.00
Description: Defective luxury item
Customer Note: Product arrived damaged, merchant won't respond
```
**Expected**: Escalated due to high amount + merchant complexity

---

### Scenario 5: Borderline Fraud Case
```
Transaction ID: TXN-BORDER-005
Merchant: RegularStore
Dispute Type: fraud
Amount: 200.00
Description: Charge from known merchant but unusual amount
Customer Note: I shop here but this amount seems wrong
```
**Expected**: May auto-resolve or escalate based on fraud score (40-60 range)

---

### Scenario 6: Duplicate Transaction Dispute
```
Transaction ID: TXN-DUP-006
Merchant: CoffeShop
Dispute Type: merchant_dispute
Amount: 15.00
Description: Charged twice for same purchase
Customer Note: I was charged twice, have receipt for single purchase
```
**Expected**: Escalated for merchant verification

---

## 🔍 **What to Verify in Each Scenario**

### ✅ UI Elements
- [ ] Form submission works
- [ ] Result page shows complete case details
- [ ] Audit trail displays all agent steps
- [ ] Timestamps are accurate
- [ ] Review queue shows pending cases
- [ ] Navigation between pages works

### ✅ Agent Behavior (ReAct Pattern)
- [ ] **THINK**: Agent reasoning is clear and logical
- [ ] **ACT**: Actions taken are properly logged
- [ ] **OBSERVE**: Observations from other agents are captured
- [ ] **DECIDE**: Final decision includes detailed explanation

### ✅ Business Logic
- [ ] Fraud cases with score >70 → Auto-approve refund
- [ ] Fraud cases with score <30 → Auto-reject (legitimate)
- [ ] Merchant disputes → Always escalate to human
- [ ] High amounts (>$1000) → Escalate regardless of type
- [ ] Compliance checks pass for all decisions
- [ ] Audit trail is complete and immutable

### ✅ Human-in-the-Loop
- [ ] Cases appear in review queue immediately
- [ ] Reviewer can see full agent context
- [ ] Decisions are properly recorded
- [ ] Audit trail includes reviewer actions
- [ ] Status updates correctly after review

### ✅ Data Integrity
- [ ] Case IDs are unique
- [ ] Timestamps are accurate
- [ ] All fields are properly saved
- [ ] Concurrent access works (test with multiple cases)

---

## 📊 **Expected Response Times**

| Scenario | Expected Time | Notes |
|----------|---------------|-------|
| Auto-resolve (fraud) | 3-5 seconds | Depends on LLM response time |
| Auto-reject (fraud) | 3-5 seconds | Depends on LLM response time |
| Escalate (merchant) | 2-4 seconds | Faster as no complex analysis |
| Human review | Manual | 30-60 seconds typical |

---

## 🎭 **Testing Different LLM Providers**

### With Anthropic Claude
```bash
export ANTHROPIC_API_KEY=your_key_here
# Set in application.properties: llm.provider=anthropic
mvn spring-boot:run
```
**Expected**: More detailed reasoning, better context understanding

### With Ollama (Local)
```bash
ollama pull llama2
# Set in application.properties: llm.provider=ollama
mvn spring-boot:run
```
**Expected**: Faster responses, works offline, may be less detailed

---

## 🐛 **Troubleshooting**

### LLM Connection Issues
**Problem**: "Failed to connect to LLM service"
**Solutions**:
- Check `ANTHROPIC_API_KEY` is set correctly
- Verify Ollama is running: `ollama list`
- Check network connectivity
- Review application logs for detailed error

### UI Not Loading
**Problem**: Blank page or 404 errors
**Solutions**:
- Verify port 8080 is available: `lsof -i :8080`
- Check Spring Boot startup logs
- Ensure templates exist in `src/main/resources/templates/`
- Clear browser cache

### Incorrect Decisions
**Problem**: Agent decisions don't match expected behavior
**Solutions**:
- Review agent prompts in `Prompts.java`
- Check LLM provider responses in logs
- Verify fraud scoring logic in `FraudDetectionAgent.java`
- Test with different LLM providers

### Performance Issues
**Problem**: Slow response times (>10 seconds)
**Solutions**:
- Check LLM API rate limits
- Monitor network latency
- Consider using Ollama for faster local processing
- Review agent orchestration logic

---

## 📝 **Test Checklist**

Use this checklist to verify all functionality:

- [ ] **Scenario 1**: Fraud auto-approved
- [ ] **Scenario 2**: Merchant dispute escalated
- [ ] **Scenario 3**: Low-risk fraud rejected
- [ ] **Scenario 4**: High-value escalated
- [ ] **Scenario 5**: Borderline case handled
- [ ] **Scenario 6**: Duplicate transaction escalated
- [ ] Review queue displays pending cases
- [ ] Human review workflow completes
- [ ] Audit trail is complete
- [ ] All agent steps are visible
- [ ] Timestamps are accurate
- [ ] Navigation works correctly
- [ ] Error handling works (test with invalid inputs)
- [ ] Concurrent cases work (submit multiple)

---

## 🚀 **Quick Start Commands**

```bash
# Start application
mvn spring-boot:run

# Access main page
open http://localhost:8080

# Access review queue
open http://localhost:8080/review-queue

# View logs
tail -f logs/application.log

# Stop application
Ctrl+C
```

---

## 📚 **Additional Resources**

- **Architecture**: See `UC4_Dispute_Management_Implementation_Spec.md`
- **API Documentation**: Check `README.md`
- **Agent Prompts**: Review `src/main/java/com/demo/dispute/config/Prompts.java`
- **Model Classes**: See `src/main/java/com/demo/dispute/model/`

---

## 💡 **Tips for Demo**

1. **Start with Scenario 1** (fraud) - most impressive auto-resolution
2. **Show Scenario 2** (merchant) - demonstrates human-in-loop
3. **Highlight agent reasoning** - show the ReAct pattern in action
4. **Demonstrate review queue** - show human oversight capability
5. **Test edge cases** - show robustness with unusual inputs
6. **Compare LLM providers** - show flexibility of architecture

---

**Last Updated**: 2026-04-21
**Version**: 1.0.0
