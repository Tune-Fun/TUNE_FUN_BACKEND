package com.tune_fun.v1.interaction.application.service;

import com.tune_fun.v1.common.stereotype.UseCase;
import com.tune_fun.v1.interaction.application.port.input.query.FollowingQuery;
import com.tune_fun.v1.interaction.application.port.output.LoadFollowPort;
import com.tune_fun.v1.interaction.domain.ScrollableFollowInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Window;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

@Service
@UseCase
@RequiredArgsConstructor
public class FollowQueryService implements FollowingQuery {

    private final LoadFollowPort loadFollowPort;


    @Override
    public Window<ScrollableFollowInfo> scrollFollowingUser(final Long lastId, final User user) {
        return null;
    }

}
