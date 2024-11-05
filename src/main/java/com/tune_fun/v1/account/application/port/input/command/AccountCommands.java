package com.tune_fun.v1.account.application.port.input.command;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class AccountCommands {

    public record Register(
            @NotBlank(message = "{username.not_blank}")
            String username,
            @NotBlank(message = "{password.not_blank}")
            String password,
            @NotBlank(message = "{email.not_blank}")
            @Email(message = "{email.format}")
            String email,
            @NotBlank(message = "{nickname.not_blank}")
            String nickname,

            @Valid
            Notification notification
    ) {
    }

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record Notification(
            @NotNull(message = "{notification.vote_progress.not_null}")
            Boolean voteProgressNotification,
            @NotNull(message = "{notification.vote_end.not_null}")
            Boolean voteEndNotification,
            @NotNull(message = "{notification.vote_delivery.not_null}")
            Boolean voteDeliveryNotification
    ) {
    }

    public record Login(
            @NotBlank(message = "{username.not_blank}")
            String username,
            @NotBlank(message = "{password.not_blank}")
            String password,
            @Valid
            Device device
    ) {
    }

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record Device(
            @NotBlank(message = "{fcm_token.not_blank}")
            String fcmToken,
            @NotBlank(message = "{device_token.not_blank}")
            String deviceToken
    ) {
    }

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record Refresh(
            @NotBlank(message = "{refresh_token.not_blank}")
            String refreshToken) {
    }

    public record SendForgotPasswordOtp(@NotBlank(message = "{username.not_blank}") String username) {
    }

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record SetNewPassword(@NotBlank(message = "{new_password.not_blank}") String newPassword) {
    }

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record UpdateNickname(@NotBlank(message = "{new_nickname.not_blank}") String newNickname) {
    }

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record SaveEmail(@NotBlank(message = "{email.not_blank}") String email) {
    }

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record CancelAccount(@NotBlank(message = "{otp.not_blank}") String otp) {
    }
}
