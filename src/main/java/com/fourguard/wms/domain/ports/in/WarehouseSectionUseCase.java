package com.fourguard.wms.domain.ports.in;

import com.fourguard.wms.application.dto.request.CreateWarehouseSectionRequest;
import com.fourguard.wms.application.dto.request.UpdateWarehouseSectionRequest;
import com.fourguard.wms.application.dto.response.WarehouseSectionResponse;

import java.util.List;
import java.util.UUID;

public interface WarehouseSectionUseCase {
    WarehouseSectionResponse createWarehouseSection(CreateWarehouseSectionRequest request);
    WarehouseSectionResponse updateWarehouseSection(UpdateWarehouseSectionRequest request);
    WarehouseSectionResponse getWarehouseSectionById(UUID id);
    List<WarehouseSectionResponse> getWarehouseSectionsByBranchId(UUID branchId);
    List<WarehouseSectionResponse> getAllWarehouseSections();
    void deleteWarehouseSection(UUID id);
}
