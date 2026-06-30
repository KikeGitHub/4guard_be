package com.fourguard.wms.infrastructure.persistence.entity;

import com.fourguard.wms.domain.enums.BranchStatus;
import com.fourguard.wms.shared.audit.BaseVersionedEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "branches", schema = "wms",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_branch_org_code",
                columnNames = {"organization_id", "code"}
        ))
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class BranchEntity extends BaseVersionedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false, columnDefinition = "UUID")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organization_id", nullable = false)
    private OrganizationEntity organization;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false, length = 20)
    private String code;

    @Column(length = 50)
    @Builder.Default
    private String timezone = "UTC";

    @Column(name = "address_line1", columnDefinition = "TEXT")
    private String addressLine1;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @Builder.Default
    private BranchStatus status = BranchStatus.ACTIVE;

    @OneToMany(mappedBy = "branch", fetch = FetchType.LAZY)
    @Builder.Default
    private List<UserEntity> users = new ArrayList<>();

    @OneToMany(mappedBy = "branch", fetch = FetchType.LAZY)
    @Builder.Default
    private List<WarehouseSectionEntity> sections = new ArrayList<>();

    @OneToMany(mappedBy = "branch", fetch = FetchType.LAZY)
    @Builder.Default
    private List<LocationEntity> locations = new ArrayList<>();
}
