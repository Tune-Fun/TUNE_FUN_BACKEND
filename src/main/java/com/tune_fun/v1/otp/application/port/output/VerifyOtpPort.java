package com.tune_fun.v1.otp.application.port.output;

import com.tune_fun.v1.otp.domain.behavior.VerifyOtp;

public interface VerifyOtpPort {
    void verifyOtp(final VerifyOtp verifyOtp) throws Exception;

    void verifyOtpAndExpire(final VerifyOtp verifyOtp) throws Exception;
}
