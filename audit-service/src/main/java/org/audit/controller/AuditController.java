package org.audit.controller;

import org.audit.model.AuditEntry;
import org.audit.storage.AuditStorage;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/audit")
public class AuditController {

    private final AuditStorage auditStorage;

    public AuditController(AuditStorage auditStorage) {
        this.auditStorage = auditStorage;
    }

    @GetMapping
    public Map<String, Object> getAuditLog(
            @RequestParam(defaultValue = "100") int limit) {

        List<AuditEntry> entries = auditStorage.findLatest(limit);

        return Map.of(
                "totalEntries", auditStorage.count(),
                "showing", entries.size(),
                "entries", entries
        );
    }
}
