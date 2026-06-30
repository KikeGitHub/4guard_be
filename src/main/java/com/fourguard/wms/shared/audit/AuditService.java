package com.fourguard.wms.shared.audit;

import com.fourguard.wms.domain.ports.out.AuditLogRepositoryPort;
import com.fourguard.wms.infrastructure.persistence.entity.AuditLogEntity;
import com.fourguard.wms.infrastructure.persistence.entity.UserEntity;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepositoryPort auditLogRepositoryPort;

    /**
     * Persists an audit log entry in the database.
     * Automatically extracts IP Address and User-Agent from the request context.
     */
    public void log(UserEntity actor, String action, String entityType, UUID entityId, 
                    Map<String, Object> beforeState, Map<String, Object> afterState) {
        if (actor == null) return;
        
        String ipAddress = "unknown";
        String userAgent = "unknown";

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            ipAddress = request.getHeader("X-Forwarded-For");
            if (ipAddress == null || ipAddress.isBlank()) {
                ipAddress = request.getRemoteAddr();
            }
            userAgent = request.getHeader("User-Agent");
        }

        AuditLogEntity logEntry = AuditLogEntity.builder()
                .organizationId(actor.getOrganization().getId())
                .branchId(actor.getBranch() != null ? actor.getBranch().getId() : null)
                .userId(actor.getId())
                .action(action)
                .entityType(entityType)
                .entityId(entityId)
                .beforeState(beforeState)
                .afterState(afterState)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .build();

        auditLogRepositoryPort.log(logEntry);
    }
}
