package com.tune_fun.v1.account.application.service;

import com.tune_fun.v1.account.application.port.input.command.AccountCommands;
import com.tune_fun.v1.account.application.port.output.LoadAccountPort;
import com.tune_fun.v1.account.application.port.output.RecordLastLoginAtPort;
import com.tune_fun.v1.account.application.port.output.device.SaveDevicePort;
import com.tune_fun.v1.account.application.port.output.jwt.CreateAccessTokenPort;
import com.tune_fun.v1.account.application.port.output.jwt.CreateRefreshTokenPort;
import com.tune_fun.v1.account.domain.value.RegisteredAccount;
import com.tune_fun.v1.common.exception.CommonApplicationException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class LoginServiceTest {

    @Mock
    private LoadAccountPort loadAccountPort;

    @Mock
    private RecordLastLoginAtPort recordLastLoginAtPort;

    @Mock
    private CreateAccessTokenPort createAccessTokenPort;

    @Mock
    private CreateRefreshTokenPort createRefreshTokenPort;

    @Mock
    private SaveDevicePort saveDevicePort;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private LoginService loginService;

    @Test
    @Order(1)
    @DisplayName("Username(아이디)가 존재하지 않는 경우 로그인에 실패한다.")
    void login_usernameNotFound_throwsException() {
        // given
        AccountCommands.Device device = new AccountCommands.Device("fcmToken", "deviceToken");
        AccountCommands.Login command = new AccountCommands.Login("username", "password", device);

        // when
        when(loadAccountPort.registeredAccountInfoByUsername(command.username())).thenReturn(Optional.empty());

        // then
        CommonApplicationException exception = assertThrows(CommonApplicationException.class, () -> loginService.login(command));

        assertEquals(CommonApplicationException.ACCOUNT_NOT_FOUND, exception);

        verify(loadAccountPort, times(1)).registeredAccountInfoByUsername(command.username());
        verify(passwordEncoder, never()).matches(any(), any());
        verify(createAccessTokenPort, never()).createAccessToken(any());
        verify(createRefreshTokenPort, never()).createRefreshToken(any());
        verify(recordLastLoginAtPort, never()).recordLastLoginAt(any());
        verify(saveDevicePort, never()).saveDevice(any());
    }

    @Test
    @Order(2)
    @DisplayName("비밀번호가 일치하지 않는 경우 로그인에 실패한다.")
    void login_passwordNotMatch_throwsException() {
        // given
        AccountCommands.Device device = new AccountCommands.Device("fcmToken", "deviceToken");
        AccountCommands.Login command = new AccountCommands.Login("username", "password", device);

        // when
        Optional<RegisteredAccount> thenReturnAccount =
                Optional.of(new RegisteredAccount(1L, "username", "password", "nickname",
                        "email", "profileImageUrl", Set.of("ROLE_NORMAL"), List.of()));
        when(loadAccountPort.registeredAccountInfoByUsername(command.username())).thenReturn(thenReturnAccount);
        when(passwordEncoder.matches(command.password(), "password")).thenReturn(false);

        // then
        CommonApplicationException exception = assertThrows(CommonApplicationException.class, () -> loginService.login(command));

        assertEquals(CommonApplicationException.ACCOUNT_NOT_FOUND, exception);

        verify(loadAccountPort, times(1)).registeredAccountInfoByUsername(command.username());
        verify(passwordEncoder, times(1)).matches(command.password(), "password");
        verify(createAccessTokenPort, never()).createAccessToken(any());
        verify(createRefreshTokenPort, never()).createRefreshToken(any());
        verify(recordLastLoginAtPort, never()).recordLastLoginAt(any());
        verify(saveDevicePort, never()).saveDevice(any());
    }


}
