package com.demo.dispute.store;

import com.demo.dispute.model.DisputeCase;
import com.demo.dispute.model.DisputeStatus;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

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
