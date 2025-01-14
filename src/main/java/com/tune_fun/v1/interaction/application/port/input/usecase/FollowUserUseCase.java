package com.tune_fun.v1.interaction.application.port.input.usecase;

import com.tune_fun.v1.interaction.application.port.input.command.InteractionCommands;
import org.springframework.security.core.userdetails.User;

@FunctionalInterface
public interface FollowUserUseCase {
    void follow(final InteractionCommands.Follow command, final User user);
}
