package com.tune_fun.v1.account.adapter.output.persistence;

import com.tune_fun.v1.common.entity.BaseEntity;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;

@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@Table(name = "authority")
public class AuthorityJpaEntity extends BaseEntity {

    @Id
    @Tsid
    @Column(name = "id", nullable = false, updatable = false)
    @Comment("TSID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false, updatable = false, referencedColumnName = "id")
    private AccountJpaEntity account;

    @Column(name = "role", nullable = false)
    @Comment("권한")
    private Role role;

}
