package com.tune_fun.v1.account.domain.value;

import com.tune_fun.v1.account.domain.value.oauth2.RegisteredOAuth2Account;

import java.util.List;
import java.util.Set;

public record RegisteredAccount(
        Long id,
        String username,
        String password,
        String email,
        String nickname,
        String profileImageUrl,
        Set<String> roles,
        List<RegisteredOAuth2Account> oauth2Accounts
) implements Account {

    public boolean isUniqueOAuth2Account() {
        return oauth2Accounts.stream()
                .filter(RegisteredOAuth2Account::enabled)
                .count() == 1;
    }

}
