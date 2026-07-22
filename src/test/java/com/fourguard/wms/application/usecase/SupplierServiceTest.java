package com.fourguard.wms.application.usecase;

import com.fourguard.wms.application.dto.request.CreateSupplierRequest;
import com.fourguard.wms.application.dto.request.SupplierContactRequest;
import com.fourguard.wms.application.dto.request.UpdateSupplierStatusRequest;
import com.fourguard.wms.application.dto.response.SupplierResponse;
import com.fourguard.wms.application.mapper.SupplierMapper;
import com.fourguard.wms.domain.enums.SupplierScopeType;
import com.fourguard.wms.domain.enums.SupplierStatus;
import com.fourguard.wms.domain.exception.ValidationException;
import com.fourguard.wms.domain.ports.out.SupplierRepositoryPort;
import com.fourguard.wms.infrastructure.persistence.entity.OrganizationEntity;
import com.fourguard.wms.infrastructure.persistence.entity.SupplierContactEntity;
import com.fourguard.wms.infrastructure.persistence.entity.SupplierEntity;
import com.fourguard.wms.infrastructure.persistence.repository.OrganizationJpaRepository;
import com.fourguard.wms.shared.audit.AuditService;
import com.fourguard.wms.shared.audit.SecurityAuditHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SupplierServiceTest {

    @Mock
    private SupplierRepositoryPort supplierRepositoryPort;
    @Mock
    private OrganizationJpaRepository organizationJpaRepository;
    @Mock
    private SupplierMapper supplierMapper;
    @Mock
    private SecurityAuditHelper securityAuditHelper;
    @Mock
    private AuditService auditService;

    @InjectMocks
    private SupplierService supplierService;

    private UUID orgId;
    private OrganizationEntity orgEntity;
    private CreateSupplierRequest createRequest;
    private SupplierEntity supplierEntity;
    private SupplierResponse supplierResponse;

    @BeforeEach
    void setUp() {
        orgId = UUID.randomUUID();
        orgEntity = OrganizationEntity.builder().id(orgId).name("Test Org").build();

        SupplierContactRequest contactRequest = SupplierContactRequest.builder()
                .fullName("Carlos Mendoza")
                .email("cmendoza@empaquesnorte.com.mx")
                .phone("8183456789")
                .build();

        createRequest = CreateSupplierRequest.builder()
                .organizationId(orgId)
                .legalName("Empaques Nacionales del Norte S.A. de C.V.")
                .taxId("ENN980415HG8")
                .type("PACKAGING")
                .scopeType("GLOBAL")
                .contact(contactRequest)
                .build();

        supplierEntity = SupplierEntity.builder()
                .id(UUID.randomUUID())
                .organization(orgEntity)
                .code("PRV-0001")
                .legalName(createRequest.getLegalName())
                .taxId(createRequest.getTaxId())
                .supplierTypeCode(createRequest.getType())
                .status(SupplierStatus.ACTIVE)
                .scopeType(SupplierScopeType.GLOBAL)
                .build();

        supplierResponse = SupplierResponse.builder()
                .id(supplierEntity.getId())
                .organizationId(orgId)
                .code("PRV-0001")
                .legalName(createRequest.getLegalName())
                .taxId(createRequest.getTaxId())
                .type(createRequest.getType())
                .status("ACTIVE")
                .scopeType("GLOBAL")
                .build();
    }

    @Test
    void whenCreateSupplier_withValidData_thenSuccess() {
        // Arrange
        when(organizationJpaRepository.findById(orgId)).thenReturn(Optional.of(orgEntity));
        when(supplierRepositoryPort.existsByOrganizationIdAndTaxIdAndIsDeletedFalse(orgId, createRequest.getTaxId())).thenReturn(false);
        when(supplierRepositoryPort.findMaxCodeSequence(orgId)).thenReturn(Optional.of(0));
        when(supplierMapper.toEntity(createRequest)).thenReturn(supplierEntity);
        when(supplierMapper.toContactEntity(any())).thenReturn(SupplierContactEntity.builder().fullName("Carlos Mendoza").build());
        when(securityAuditHelper.getCurrentUsername()).thenReturn("admin");
        when(supplierRepositoryPort.save(any(SupplierEntity.class))).thenReturn(supplierEntity);
        when(supplierMapper.toResponse(any(SupplierEntity.class))).thenReturn(supplierResponse);

        // Act
        SupplierResponse response = supplierService.createSupplier(createRequest);

        // Assert
        assertNotNull(response);
        assertEquals("PRV-0001", response.getCode());
        verify(supplierRepositoryPort, times(1)).save(any(SupplierEntity.class));
    }

    @Test
    void whenCreateSupplier_withDuplicateTaxId_thenThrowsValidationException() {
        // Arrange
        when(organizationJpaRepository.findById(orgId)).thenReturn(Optional.of(orgEntity));
        when(supplierRepositoryPort.existsByOrganizationIdAndTaxIdAndIsDeletedFalse(orgId, createRequest.getTaxId())).thenReturn(true);

        // Act & Assert
        assertThrows(ValidationException.class, () -> supplierService.createSupplier(createRequest));
        verify(supplierRepositoryPort, never()).save(any());
    }

    @Test
    void whenUpdateSupplierStatus_toInactiveWithoutReason_thenThrowsValidationException() {
        // Arrange
        UUID supplierId = UUID.randomUUID();

        UpdateSupplierStatusRequest request = UpdateSupplierStatusRequest.builder()
                .status("INACTIVE")
                .reason("") // Empty reason
                .build();

        // Act & Assert
        assertThrows(ValidationException.class, () -> supplierService.updateSupplierStatus(supplierId, request));
    }
}
