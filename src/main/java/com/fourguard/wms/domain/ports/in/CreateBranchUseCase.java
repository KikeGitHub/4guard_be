package com.fourguard.wms.domain.ports.in;

import com.fourguard.wms.application.dto.request.CreateBranchRequest;
import com.fourguard.wms.application.dto.response.BranchResponse;

public interface CreateBranchUseCase {
    BranchResponse createBranch(CreateBranchRequest request);
}
