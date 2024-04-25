package com.tune_fun.v1.account.adapter.output.persistence;

import com.tune_fun.v1.account.adapter.output.persistence.oauth2.OAuth2AccountJpaEntity;
import com.tune_fun.v1.account.domain.behavior.SaveAccount;
import com.tune_fun.v1.account.domain.value.CurrentAccount;
import com.tune_fun.v1.account.domain.value.RegisteredAccount;
import com.tune_fun.v1.account.domain.value.oauth2.RegisteredOAuth2Account;
import com.tune_fun.v1.common.config.BaseMapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Collections;
import java.util.Set;

@Mapper(
        config = BaseMapperConfig.class,
        imports = {Collections.class, Role.class}
)
public abstract class AccountMapper {

    @Mapping(target = "roles", source = "roles", qualifiedByName = "roleValues")
    public abstract CurrentAccount accountInfo(final AccountJpaEntity accountJpaEntity);

    @Mapping(target = "roles", expression = "java(Collections.singletonList(Role.CLIENT_0))")
    @Mapping(target = "notificationConfig.voteProgressNotification", source = "voteProgressNotification")
    @Mapping(target = "notificationConfig.voteEndNotification", source = "voteEndNotification")
    @Mapping(target = "notificationConfig.voteDeliveryNotification", source = "voteDeliveryNotification")
    public abstract AccountJpaEntity fromSaveAccountBehavior(final SaveAccount behavior);

    @Mapping(target = "roles", source = "roles", qualifiedByName = "roleValues")
    public abstract RegisteredAccount registeredAccountInfo(final AccountJpaEntity accountJpaEntity);

    @Named("roleValues")
    public Set<String> roleValues(Set<Role> roles) {
        return Role.roleValues(roles);
    }

    public abstract RegisteredOAuth2Account registeredOAuth2AccountInfo(final OAuth2AccountJpaEntity oAuth2AccountJpaEntity);
}
