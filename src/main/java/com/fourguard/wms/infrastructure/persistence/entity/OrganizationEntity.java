package com.fourguard.wms.infrastructure.persistence.entity;

import com.fourguard.wms.domain.enums.OrganizationStatus;
import com.fourguard.wms.domain.enums.OrganizationType;
import com.fourguard.wms.shared.audit.BaseVersionedEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "organizations", schema = "wms",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_org_code",   columnNames = "code"),
                @UniqueConstraint(name = "uk_org_tax_id", columnNames = "tax_id")
        })
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class OrganizationEntity extends BaseVersionedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false, columnDefinition = "UUID")
    private UUID id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(unique = true, nullable = false, length = 20)
    private String code;

    @Column(name = "tax_id", unique = true, length = 20)
    private String taxId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private OrganizationType type;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @Builder.Default
    private OrganizationStatus status = OrganizationStatus.ACTIVE;

    @OneToMany(mappedBy = "organization", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrganizationSettingEntity> settings = new ArrayList<>();

    @OneToMany(mappedBy = "organization", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Builder.Default
    private List<BranchEntity> branches = new ArrayList<>();

    @OneToMany(mappedBy = "organization", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Builder.Default
    private List<ClientEntity> clients = new ArrayList<>();

    @OneToMany(mappedBy = "organization", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Builder.Default
    private List<UserEntity> users = new ArrayList<>();
}
