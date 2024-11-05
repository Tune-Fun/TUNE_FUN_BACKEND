package com.tune_fun.v1.account.application.port.input.usecase;

import com.tune_fun.v1.account.application.port.input.command.AccountCommands;

@FunctionalInterface
public interface CancelAccountUseCase {

    void cancelAccount(String username, AccountCommands.CancelAccount cancelAccount) throws Exception;
}
