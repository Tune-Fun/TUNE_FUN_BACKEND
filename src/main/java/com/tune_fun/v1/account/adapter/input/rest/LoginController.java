package com.tune_fun.v1.account.adapter.input.rest;

import com.tune_fun.v1.account.application.port.input.command.AccountCommands;
import com.tune_fun.v1.account.application.port.input.usecase.LoginUseCase;
import com.tune_fun.v1.account.domain.value.LoginResult;
import com.tune_fun.v1.common.config.Uris;
import com.tune_fun.v1.common.response.Response;
import com.tune_fun.v1.common.response.ResponseMapper;
import com.tune_fun.v1.common.stereotype.WebAdapter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@WebAdapter
@RequiredArgsConstructor
public class LoginController {

    private final LoginUseCase loginUseCase;
    private final ResponseMapper responseMapper;

    @PostMapping(value = Uris.LOGIN)
    public ResponseEntity<Response<LoginResult>> login(@Valid @RequestBody final AccountCommands.Login command) {
        LoginResult loginResult = loginUseCase.login(command);
        return responseMapper.ok(loginResult);
    }

}
