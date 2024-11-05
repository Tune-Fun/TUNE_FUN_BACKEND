package com.tune_fun.v1.account.application.service;

import com.tune_fun.v1.account.application.port.input.command.AccountCommands;
import com.tune_fun.v1.account.application.port.input.usecase.CancelAccountUseCase;
import com.tune_fun.v1.account.application.port.output.DisableAccountPort;
import com.tune_fun.v1.account.application.port.output.LoadAccountPort;
import com.tune_fun.v1.account.application.utils.AccountMaskingUtils;
import com.tune_fun.v1.account.domain.value.MaskedAccount;
import com.tune_fun.v1.account.domain.value.RegisteredAccount;
import com.tune_fun.v1.common.exception.CommonApplicationException;
import com.tune_fun.v1.otp.application.port.output.VerifyOtpPort;
import com.tune_fun.v1.otp.domain.behavior.OtpType;
import com.tune_fun.v1.otp.domain.behavior.VerifyOtp;
import com.tune_fun.v1.vote.application.port.output.DeleteVotePaperPort;
import com.tune_fun.v1.vote.application.port.output.LoadVotePaperPort;
import com.tune_fun.v1.vote.domain.value.RegisteredVotePaper;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CancelAccountService implements CancelAccountUseCase {

    private final VerifyOtpPort verifyOtpPort;
    private final LoadVotePaperPort loadVotePaperPort;
    private final DeleteVotePaperPort deleteVotePaperPort;
    private final DisableAccountPort disableAccountPort;
    private final LoadAccountPort loadAccountPort;

    @Override
    public void cancelAccount(String username, AccountCommands.CancelAccount cancelAccountCommand) throws Exception {
        VerifyOtp verifyOtp = new VerifyOtp(username, OtpType.ACCOUNT_CANCELLATION, cancelAccountCommand.otp());
        verifyOtpPort.verifyOtpAndExpire(verifyOtp);

        List<Long> votePaperIds = loadVotePaperPort.loadRegisteredVotePapers(username).stream()
                .map(RegisteredVotePaper::id)
                .toList();

        deleteVotePaperPort.disableVotePapers(votePaperIds);

        RegisteredAccount registeredAccount = loadAccountPort.registeredAccountInfoByUsername(username)
                .orElseThrow(() -> CommonApplicationException.ACCOUNT_NOT_FOUND);
        MaskedAccount maskedAccount = getMaskedAccount(registeredAccount);
        disableAccountPort.disableAccount(maskedAccount);
    }

    private @NotNull MaskedAccount getMaskedAccount(RegisteredAccount registeredAccount) {
        return new MaskedAccount(
                registeredAccount.id(),
                AccountMaskingUtils.maskUsername(registeredAccount.username()),
                AccountMaskingUtils.maskAll(registeredAccount.password()),
                AccountMaskingUtils.maskEmail(registeredAccount.email()),
                AccountMaskingUtils.maskNickname(registeredAccount.nickname()),
                AccountMaskingUtils.maskAll(registeredAccount.profileImageUrl())
        );
    }
}
