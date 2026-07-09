package com.fourguard.wms.domain.ports.in;

import java.util.UUID;

public interface DeleteBranchUseCase {
    void deleteBranch(UUID id);
}
