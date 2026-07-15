package com.fourguard.wms.application.usecase;

import com.fourguard.wms.application.dto.request.CreateProductSkuRequest;
import com.fourguard.wms.application.dto.request.UpdateProductSkuRequest;
import com.fourguard.wms.application.dto.response.ProductSkuResponse;
import com.fourguard.wms.application.mapper.ProductSkuMapper;
import com.fourguard.wms.domain.exception.EntityNotFoundException;
import com.fourguard.wms.domain.exception.ValidationException;
import com.fourguard.wms.domain.ports.in.ProductSkuUseCase;
import com.fourguard.wms.domain.ports.out.ClientRepositoryPort;
import com.fourguard.wms.domain.ports.out.ProductSkuRepositoryPort;
import com.fourguard.wms.infrastructure.persistence.entity.ClientEntity;
import com.fourguard.wms.infrastructure.persistence.entity.ProductSkuEntity;
import com.fourguard.wms.shared.audit.SecurityAuditHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductSkuService implements ProductSkuUseCase {

    private final ProductSkuRepositoryPort productSkuRepositoryPort;
    private final ClientRepositoryPort clientRepositoryPort;
    private final ProductSkuMapper productSkuMapper;
    private final SecurityAuditHelper securityAuditHelper;

    @Override
    @Transactional
    public ProductSkuResponse createProductSku(CreateProductSkuRequest request) {
        log.info("Creating SKU: {} for client: {}", request.getCode(), request.getClientId());
        ClientEntity client = clientRepositoryPort.findById(request.getClientId())
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado con ID: " + request.getClientId()));

        if (productSkuRepositoryPort.findByClientIdAndCode(request.getClientId(), request.getCode()).isPresent()) {
            throw new ValidationException("El código SKU ya existe para este cliente: " + request.getCode());
        }

        ProductSkuEntity entity = productSkuMapper.toEntity(request);
        entity.setClient(client);
        entity.setCreatedBy(securityAuditHelper.getCurrentUsername());
        ProductSkuEntity saved = productSkuRepositoryPort.save(entity);
        return productSkuMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public ProductSkuResponse updateProductSku(UpdateProductSkuRequest request) {
        log.info("Updating SKU with ID: {}", request.getId());
        ProductSkuEntity existing = productSkuRepositoryPort.findById(request.getId())
                .orElseThrow(() -> new EntityNotFoundException("SKU no encontrado con ID: " + request.getId()));

        ClientEntity client = clientRepositoryPort.findById(request.getClientId())
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado con ID: " + request.getClientId()));

        if (!existing.getCode().equals(request.getCode()) &&
                productSkuRepositoryPort.findByClientIdAndCode(request.getClientId(), request.getCode()).isPresent()) {
            throw new ValidationException("El código SKU ya existe para este cliente: " + request.getCode());
        }

        productSkuMapper.updateEntityFromDto(request, existing);
        existing.setClient(client);
        existing.setUpdatedBy(securityAuditHelper.getCurrentUsername());
        ProductSkuEntity saved = productSkuRepositoryPort.save(existing);
        return productSkuMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductSkuResponse getProductSkuById(UUID id) {
        log.debug("Fetching SKU with ID: {}", id);
        return productSkuRepositoryPort.findById(id)
                .map(productSkuMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("SKU no encontrado con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductSkuResponse> getProductSkusByClientId(UUID clientId) {
        log.debug("Fetching SKUs by client ID: {}", clientId);
        return productSkuRepositoryPort.findByClientId(clientId).stream()
                .map(productSkuMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductSkuResponse> getAllProductSkus() {
        log.debug("Fetching all SKUs");
        return productSkuRepositoryPort.findAll().stream()
                .map(productSkuMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteProductSku(UUID id) {
        log.info("Deleting SKU with ID: {}", id);
        if (!productSkuRepositoryPort.findById(id).isPresent()) {
            throw new EntityNotFoundException("SKU no encontrado con ID: " + id);
        }
        productSkuRepositoryPort.deleteById(id);
    }
}
