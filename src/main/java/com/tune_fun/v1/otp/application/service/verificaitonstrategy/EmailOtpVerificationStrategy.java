package com.tune_fun.v1.otp.application.service.verificaitonstrategy;

import com.tune_fun.v1.account.application.port.output.LoadAccountPort;
import com.tune_fun.v1.account.application.port.output.RecordEmailVerifiedAtPort;
import com.tune_fun.v1.account.application.port.output.jwt.CreateAccessTokenPort;
import com.tune_fun.v1.account.application.port.output.jwt.CreateRefreshTokenPort;
import com.tune_fun.v1.account.domain.behavior.SaveJwtToken;
import com.tune_fun.v1.account.domain.value.CurrentAccount;
import com.tune_fun.v1.common.exception.CommonApplicationException;
import com.tune_fun.v1.otp.domain.behavior.OtpType;
import com.tune_fun.v1.otp.application.port.output.VerifyOtpPort;
import com.tune_fun.v1.otp.domain.behavior.VerifyOtp;
import com.tune_fun.v1.otp.domain.value.EmailVerifyResult;
import com.tune_fun.v1.otp.domain.value.VerifyResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class EmailOtpVerificationStrategy extends BaseOtpVerificationStrategy {

    private final LoadAccountPort loadAccountPort;
    private final VerifyOtpPort verifyOtpPort;
    private final RecordEmailVerifiedAtPort recordEmailVerifiedAtPort;
    private final CreateAccessTokenPort createAccessTokenPort;
    private final CreateRefreshTokenPort createRefreshTokenPort;

    @Override
    public VerifyResult verifyOtp(VerifyOtp verifyOtp) throws Exception {
        verifyOtpPort.verifyOtpAndExpire(verifyOtp);

        recordEmailVerifiedAtPort.recordEmailVerifiedAt(verifyOtp.username());

        CurrentAccount currentAccount = getCurrentAccount(verifyOtp.username());
        SaveJwtToken saveJwtTokenBehavior = getSaveJwtTokenBehavior(currentAccount);

        String accessToken = createAccessTokenPort.createAccessToken(saveJwtTokenBehavior);
        String refreshToken = createRefreshTokenPort.createRefreshToken(saveJwtTokenBehavior);

        return new EmailVerifyResult(accessToken, refreshToken);
    }

    @Override
    public boolean matchedOtpType(OtpType otpType) {
        return Arrays.stream(OtpType.values())
                .filter(type -> type != OtpType.ACCOUNT_CANCELLATION)
                .anyMatch(type -> type == otpType);
    }

    private CurrentAccount getCurrentAccount(String username) {
        return loadAccountPort.currentAccountInfo(username)
                .orElseThrow(CommonApplicationException.ACCOUNT_NOT_FOUND);
    }
}
