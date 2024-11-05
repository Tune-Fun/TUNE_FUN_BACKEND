package com.tune_fun.v1.otp.adapter.output.persistence;

import com.tune_fun.v1.common.exception.CommonApplicationException;
import com.tune_fun.v1.common.helper.KmsEncryptionHelper;
import com.tune_fun.v1.common.stereotype.PersistenceAdapter;
import com.tune_fun.v1.common.util.StringUtil;
import com.tune_fun.v1.otp.application.port.output.DeleteOtpPort;
import com.tune_fun.v1.otp.application.port.output.LoadOtpPort;
import com.tune_fun.v1.otp.application.port.output.SaveOtpPort;
import com.tune_fun.v1.otp.application.port.output.VerifyOtpPort;
import com.tune_fun.v1.otp.domain.behavior.LoadOtp;
import com.tune_fun.v1.otp.domain.behavior.OtpType;
import com.tune_fun.v1.otp.domain.behavior.SaveOtp;
import com.tune_fun.v1.otp.domain.behavior.VerifyOtp;
import com.tune_fun.v1.otp.domain.value.CurrentDecryptedOtp;
import com.tune_fun.v1.otp.domain.value.CurrentOtp;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Date;
import java.util.Objects;

import static com.tune_fun.v1.otp.domain.behavior.OtpType.fromLabel;
import static java.lang.String.format;


@Component
@PersistenceAdapter
@RequiredArgsConstructor
public class OtpPersistenceAdapter implements SaveOtpPort, LoadOtpPort, VerifyOtpPort, DeleteOtpPort {

    private final RedisTemplate<String, OtpRedisEntity> redisTemplate;
    private final KmsEncryptionHelper kmsEncryptionHelper;

    @Value("${otp.validity}")
    private Duration otpValidity;

    @Override
    public CurrentOtp saveOtp(final SaveOtp saveOtp) throws Exception {
        OtpType otpTypeConstant = getConstant(saveOtp.otpType());
        String otpKey = setOtpKey(otpTypeConstant, saveOtp.username());
        String encryptedToken = kmsEncryptionHelper.encrypt(StringUtil.randomNumeric(6));

        OtpRedisEntity otpRedisEntity = new OtpRedisEntity(saveOtp.username(), encryptedToken);

        ValueOperations<String, OtpRedisEntity> ops = redisTemplate.opsForValue();
        ops.set(otpKey, otpRedisEntity);

        setExpiredAt(otpKey);

        return new CurrentOtp(saveOtp.username(), otpTypeConstant.getLabel(), encryptedToken);
    }

    @Override
    public CurrentDecryptedOtp loadOtp(final LoadOtp loadOtp) throws Exception {
        OtpType otpTypeConstant = getConstant(loadOtp.otpType());
        ValueOperations<String, OtpRedisEntity> ops = redisTemplate.opsForValue();
        OtpRedisEntity otpRedisEntity = ops.get(setOtpKey(otpTypeConstant, loadOtp.username()));

        String decrypted = kmsEncryptionHelper.decrypt(otpRedisEntity.getToken());
        return new CurrentDecryptedOtp(loadOtp.username(), otpTypeConstant.getLabel(), decrypted);
    }

    @Override
    public void verifyOtp(final VerifyOtp verifyOtp) throws Exception {
        String otpKey = setOtpKey(verifyOtp.otpType(), verifyOtp.username());
        ValueOperations<String, OtpRedisEntity> ops = redisTemplate.opsForValue();
        OtpRedisEntity otpRedisEntity = ops.get(otpKey);

        if (otpRedisEntity == null) throw CommonApplicationException.EXCEPTION_OTP_NOT_FOUND;

        if (checkRedisExpiration(ops, otpKey))
            throw CommonApplicationException.EXCEPTION_OTP_EXPIRED;

        if (!checkMatchValue(verifyOtp.otp(), otpRedisEntity))
            throw CommonApplicationException.EXCEPTION_OTP_NOT_MATCH;
    }

    @Override
    public void verifyOtpAndExpire(VerifyOtp verifyOtp) throws Exception {
        verifyOtp(verifyOtp);
        String otpKey = setOtpKey(verifyOtp.otpType(), verifyOtp.username());
        expire(otpKey);
    }

    @Override
    public void expire(final String otpType, final String username) {
        OtpType constant = getConstant(otpType);
        String otpKey = setOtpKey(constant, username);
        expire(otpKey);
    }

    private void expire(final String otpKey) {
        redisTemplate.expire(otpKey, Duration.ZERO);
    }

    private void setExpiredAt(String otpKey) {
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + otpValidity.toMillis());
        redisTemplate.expireAt(otpKey, expireDate);
    }

    private Boolean checkRedisExpiration(final ValueOperations<String, OtpRedisEntity> ops, final String key) {
        return ops.get(key) == null || redisTemplate.getExpire(key) == null || redisTemplate.getExpire(key) < 0;
    }

    private Boolean checkMatchValue(@NotNull String value, @NotNull OtpRedisEntity otpRedisEntity) throws Exception {
        String decrypted = kmsEncryptionHelper.decrypt(otpRedisEntity.getToken());
        return Objects.equals(value, decrypted);
    }

    private OtpType getConstant(final String otpType) {
        return fromLabel(otpType);
    }

    private String setOtpKey(final OtpType otpType, final String username) {
        return format("otp::%s::%s", otpType, username);
    }
}
