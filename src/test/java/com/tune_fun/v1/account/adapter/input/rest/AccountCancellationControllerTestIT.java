package com.tune_fun.v1.account.adapter.input.rest;

import com.tune_fun.v1.account.adapter.output.persistence.AccountJpaEntity;
import com.tune_fun.v1.account.application.port.input.command.AccountCommands;
import com.tune_fun.v1.base.ControllerBaseTest;
import com.tune_fun.v1.common.config.Uris;
import com.tune_fun.v1.common.response.MessageCode;
import com.tune_fun.v1.dummy.DummyService;
import com.tune_fun.v1.otp.application.port.output.LoadOtpPort;
import com.tune_fun.v1.otp.application.port.output.SaveOtpPort;
import com.tune_fun.v1.otp.domain.behavior.LoadOtp;
import com.tune_fun.v1.otp.domain.behavior.OtpType;
import com.tune_fun.v1.otp.domain.behavior.SaveOtp;
import com.tune_fun.v1.otp.domain.value.CurrentDecryptedOtp;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static com.epages.restdocs.apispec.ResourceSnippetParameters.builder;
import static com.tune_fun.v1.otp.domain.behavior.OtpType.ACCOUNT_CANCELLATION;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

class AccountCancellationControllerTestIT extends ControllerBaseTest {

    @Autowired
    private DummyService dummyService;

    @Autowired
    private LoadOtpPort loadOtpPort;

    @Autowired
    private SaveOtpPort saveOtpPort;

    @Transactional
    @Test
    @Order(1)
    @DisplayName("계정 해지, 성공")
    void cancelAccountSuccess() throws Exception {
        dummyService.initAccount();
        AccountJpaEntity defaultAccount = dummyService.getDefaultAccount();
        dummyService.login(defaultAccount);
        String username = dummyService.getDefaultUsername();

        SaveOtp saveOtp = new SaveOtp(username, OtpType.ACCOUNT_CANCELLATION.getLabel());
        saveOtpPort.saveOtp(saveOtp);

        LoadOtp loadOtpBehavior = new LoadOtp(username, ACCOUNT_CANCELLATION.getLabel());
        CurrentDecryptedOtp decryptedOtp = loadOtpPort.loadOtp(loadOtpBehavior);

        AccountCommands.CancelAccount cancelAccountCommand = new AccountCommands.CancelAccount(decryptedOtp.token());

        ResultActions resultActions = mockMvc.perform(
                        post(Uris.CANCEL_ACCOUNT)
                                .content(toJson(cancelAccountCommand))
                                .contentType(APPLICATION_JSON_VALUE)
                )
                .andExpectAll(baseAssertion(MessageCode.SUCCESS))
                .andExpect(jsonPath("$.message", notNullValue()))
                .andExpect(jsonPath("$.code", notNullValue()));


        resultActions.andDo(restDocs.document(
                        responseFields(baseResponseFields),
                        resource(
                                builder().
                                        description("계정 해지").
                                        responseFields(baseResponseFields)
                                        .build()
                        )
                )
        );
    }
}