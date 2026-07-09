package com.fourguard.wms.domain.ports.in;

import com.fourguard.wms.application.dto.request.UpdateBranchRequest;
import com.fourguard.wms.application.dto.response.BranchResponse;

public interface UpdateBranchUseCase {
    BranchResponse updateBranch(UpdateBranchRequest request);
}
