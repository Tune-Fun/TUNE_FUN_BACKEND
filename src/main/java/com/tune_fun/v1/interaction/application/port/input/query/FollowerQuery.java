package com.tune_fun.v1.interaction.application.port.input.query;

import com.tune_fun.v1.interaction.domain.ScrollableFollowInfo;
import org.springframework.data.domain.Window;
import org.springframework.security.core.userdetails.User;

public interface FollowerQuery {
    Window<ScrollableFollowInfo> fetchFollowerUser(Integer lastId, User user);
}