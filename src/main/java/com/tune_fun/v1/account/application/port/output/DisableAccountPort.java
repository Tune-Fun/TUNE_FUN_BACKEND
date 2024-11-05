package com.tune_fun.v1.account.application.port.output;

import com.tune_fun.v1.account.domain.value.MaskedAccount;

@FunctionalInterface
public interface DisableAccountPort {

    void disableAccount(MaskedAccount maskedAccount);
}
