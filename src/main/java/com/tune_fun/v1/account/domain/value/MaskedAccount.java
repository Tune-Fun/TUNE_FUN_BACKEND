package com.tune_fun.v1.account.domain.value;

public record MaskedAccount(
        Long id,
        String username,
        String password,
        String email,
        String nickname,
        String profileImageUrl
) {

}

