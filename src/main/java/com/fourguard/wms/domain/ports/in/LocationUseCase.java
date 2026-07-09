package com.fourguard.wms.domain.ports.in;

import com.fourguard.wms.application.dto.request.CreateLocationRequest;
import com.fourguard.wms.application.dto.request.UpdateLocationRequest;
import com.fourguard.wms.application.dto.response.LocationResponse;

import java.util.List;
import java.util.UUID;

public interface LocationUseCase {
    LocationResponse createLocation(CreateLocationRequest request);
    LocationResponse updateLocation(UpdateLocationRequest request);
    LocationResponse getLocationById(UUID id);
    List<LocationResponse> getLocationsByBranchId(UUID branchId);
    List<LocationResponse> getAvailableLocationsByBranchId(UUID branchId);
    List<LocationResponse> getAllLocations();
    void deleteLocation(UUID id);
}
