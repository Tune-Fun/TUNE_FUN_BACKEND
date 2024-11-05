package com.tune_fun.v1.account.adapter.output.persistence;

import com.tune_fun.v1.account.adapter.output.persistence.device.DeviceJpaEntity;
import com.tune_fun.v1.account.adapter.output.persistence.oauth2.OAuth2AccountJpaEntity;
import com.tune_fun.v1.common.entity.BaseEntity;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;


@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@Table(name = "account")
public class AccountJpaEntity extends BaseEntity implements UserDetails {

    @Id
    @Tsid
    @Column(name = "id", nullable = false, updatable = false)
    @Comment("TSID")
    private Long id;

    @Size(max = 255)
    @NotNull
    @Column(name = "uuid", nullable = false, updatable = false, unique = true)
    @Comment("고유번호")
    private String uuid;

    @Size(max = 2000)
    @Column(name = "username", length = 2000, nullable = false)
    @Comment("아이디")
    private String username;

    @Size(max = 255)
    @NotNull
    @Column(name = "password", nullable = false)
    @Comment("비밀번호")
    private String password;

    @Column(name = "email", length = 2000)
    @Comment("이메일")
    private String email;

    @Size(max = 2000)
    @NotNull
    @Column(name = "nickname", length = 2000, nullable = false)
    @Comment("닉네임")
    private String nickname;

    @Column(name = "profile_image_url", length = 2000)
    @Comment("프로필 이미지 링크")
    private String profileImageUrl;

    @Builder.Default
    @Embedded
    private NotificationConfig notificationConfig = new NotificationConfig();

    @Singular
    @Convert(converter = RoleConverter.class)
    @Column(name = "roles", nullable = false)
    private Set<Role> roles;

    @Column(name = "last_login_at")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Comment("최종 로그인 일시")
    private LocalDateTime lastLoginAt;

    @Column(name = "last_logout_at")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Comment("최종 로그아웃 일시")
    private LocalDateTime lastLogoutAt;

    @Column(name = "email_verified_at")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Comment("이메일 인증 일시")
    private LocalDateTime emailVerifiedAt;

    @Column(name = "withdrawal_at")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Comment("탈퇴 일시")
    private LocalDateTime withdrawalAt;

    @Column(name = "deleted_at")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Comment("삭제일")
    protected LocalDateTime deletedAt;

    @Singular
    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<DeviceJpaEntity> devices;

    @Singular
    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<OAuth2AccountJpaEntity> oauth2Accounts;

    @Builder.Default
    @Column(name = "account_non_expired")
    private boolean accountNonExpired = true;

    @Builder.Default
    @Column(name = "account_non_locked")
    private boolean accountNonLocked = true;

    @Builder.Default
    @Column(name = "credentials_non_expired")
    private boolean credentialsNonExpired = true;

    @Builder.Default
    @Column(name = "enabled")
    private boolean enabled = true;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }


    public boolean isAvailable() {
        return !accountNonExpired && !accountNonLocked && !credentialsNonExpired && !enabled;
    }

    public void disable() {
        enabled = false;
        withdrawalAt = LocalDateTime.now();
    }
}
