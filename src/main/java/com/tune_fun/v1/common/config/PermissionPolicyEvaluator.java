package com.tune_fun.v1.common.config;

import com.tune_fun.v1.common.exception.CommonApplicationException;
import com.tune_fun.v1.vote.application.port.output.LoadVotePaperPort;
import com.tune_fun.v1.vote.application.port.output.LoadVotePort;
import com.tune_fun.v1.vote.domain.value.RegisteredVotePaper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Slf4j
@Component
@RequiredArgsConstructor
public class PermissionPolicyEvaluator implements PermissionEvaluator {

    private final LoadVotePaperPort loadVotePaperPort;
    private final LoadVotePort loadVotePort;

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        User principal = (User) authentication.getPrincipal();

        return switch (TargetType.valueOf(targetType)) {
            case VOTE_PAPER -> hasPermissionForVotePaper(principal, targetId);
            case VOTE -> hasPermissionForVote(principal, targetId);
        };
    }

    public boolean hasPermissionForVotePaper(User principal, Serializable targetId) {
        RegisteredVotePaper registeredVotePaper = loadVotePaperPort.loadRegisteredVotePaper(principal.getUsername())
                .orElseThrow(CommonApplicationException.VOTE_POLICY_ONLY_AUTHOR_CAN_UPDATE_DELIVERY_DATE);

        if (registeredVotePaper.id().equals(targetId)) return true;

        throw CommonApplicationException.VOTE_PAPER_NOT_FOUND;
    }

    private boolean hasPermissionForVote(User principal, Serializable targetId) {
        if (loadVotePort.loadVoteByVoterAndVotePaperId(principal.getUsername(), (Long) targetId).isPresent())
            throw CommonApplicationException.VOTE_POLICY_ONE_VOTE_PER_USER;

        return true;
    }

    private enum TargetType {
        VOTE_PAPER,
        VOTE
    }
}
