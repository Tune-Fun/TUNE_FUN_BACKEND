package com.tune_fun.v1.account.adapter.input.rest;

import com.tune_fun.v1.account.application.port.input.command.AccountCommands;
import com.tune_fun.v1.account.application.port.input.usecase.CancelAccountUseCase;
import com.tune_fun.v1.account.domain.value.CurrentUser;
import com.tune_fun.v1.common.config.Uris;
import com.tune_fun.v1.common.response.BasePayload;
import com.tune_fun.v1.common.response.MessageCode;
import com.tune_fun.v1.common.response.Response;
import com.tune_fun.v1.common.response.ResponseMapper;
import com.tune_fun.v1.common.stereotype.WebAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@WebAdapter
@RequiredArgsConstructor
public class AccountCancellationController {

    private final CancelAccountUseCase cancelAccountUseCase;
    private final ResponseMapper responseMapper;

    @PostMapping(value = Uris.CANCEL_ACCOUNT)
    public ResponseEntity<Response<BasePayload>> cancelAccount(
            @RequestBody AccountCommands.CancelAccount cancelAccountCommand,
            @CurrentUser User user
    ) throws Exception {
        cancelAccountUseCase.cancelAccount(user.getUsername(), cancelAccountCommand);
        return responseMapper.ok(MessageCode.SUCCESS);
    }
}

