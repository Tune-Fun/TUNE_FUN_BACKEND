package com.tune_fun.v1.account.application.port.input.usecase;

import org.springframework.security.core.userdetails.User;

public interface CheckEmailVerifiedUseCase {
    void checkEmailVerified(final User user);
}
