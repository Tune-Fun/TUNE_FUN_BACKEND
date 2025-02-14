package com.tune_fun.v1.account.application.service;

import com.tune_fun.v1.account.application.port.input.command.AccountCommands;
import com.tune_fun.v1.account.application.port.input.usecase.LoginUseCase;
import com.tune_fun.v1.account.application.port.output.LoadAccountPort;
import com.tune_fun.v1.account.application.port.output.RecordLastLoginAtPort;
import com.tune_fun.v1.account.application.port.output.device.SaveDevicePort;
import com.tune_fun.v1.account.application.port.output.jwt.CreateAccessTokenPort;
import com.tune_fun.v1.account.application.port.output.jwt.CreateRefreshTokenPort;
import com.tune_fun.v1.account.domain.behavior.SaveDevice;
import com.tune_fun.v1.account.domain.behavior.SaveJwtToken;
import com.tune_fun.v1.account.domain.value.LoginResult;
import com.tune_fun.v1.account.domain.value.RegisteredAccount;
import com.tune_fun.v1.common.constant.Constants;
import com.tune_fun.v1.common.exception.CommonApplicationException;
import com.tune_fun.v1.common.stereotype.UseCase;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@UseCase
@RequiredArgsConstructor
public class LoginService implements LoginUseCase {

    private final LoadAccountPort loadAccountPort;
    private final RecordLastLoginAtPort recordLastLoginAtPort;
    private final CreateAccessTokenPort createAccessTokenPort;
    private final CreateRefreshTokenPort createRefreshTokenPort;
    private final SaveDevicePort saveDevicePort;

    private final PasswordEncoder passwordEncoder;

    @NotNull
    private static SaveJwtToken getSaveJwtToken(RegisteredAccount registeredAccount, String authorities) {
        return new SaveJwtToken(registeredAccount.username(), authorities);
    }

    @NotNull
    private static LoginResult getLoginResult(RegisteredAccount registeredAccount, String accessToken, String refreshToken) {
        return new LoginResult(registeredAccount.id(), registeredAccount.username(), registeredAccount.nickname(), registeredAccount.email(),
                registeredAccount.roles(), accessToken, refreshToken);
    }

    @Override
    @Transactional
    public LoginResult login(final AccountCommands.Login command) {
        RegisteredAccount registeredAccount = loadAccountPort.registeredAccountInfoByUsername(command.username())
                .orElseThrow(CommonApplicationException.ACCOUNT_NOT_FOUND);

        if (!passwordEncoder.matches(command.password(), registeredAccount.password()))
            throw CommonApplicationException.ACCOUNT_NOT_FOUND;

        String authorities = String.join(Constants.COMMA, registeredAccount.roles());

        SaveJwtToken saveJwtTokenBehavior = getSaveJwtToken(registeredAccount, authorities);

        String accessToken = createAccessTokenPort.createAccessToken(saveJwtTokenBehavior);
        String refreshToken = createRefreshTokenPort.createRefreshToken(saveJwtTokenBehavior);

        recordLastLoginAtPort.recordLastLoginAt(registeredAccount.username());
        SaveDevice saveDeviceBehavior = new SaveDevice(registeredAccount.username(), command.device().fcmToken(), command.device().deviceToken());
        saveDevicePort.saveDevice(saveDeviceBehavior);

        return getLoginResult(registeredAccount, accessToken, refreshToken);
    }
}
