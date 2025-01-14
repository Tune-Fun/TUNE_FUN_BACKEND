package com.tune_fun.v1.vote.application.service;

import com.tune_fun.v1.common.exception.CommonApplicationException;
import com.tune_fun.v1.common.stereotype.UseCase;
import com.tune_fun.v1.vote.application.port.input.usecase.RegisterVoteUseCase;
import com.tune_fun.v1.vote.application.port.output.LoadVotePort;
import com.tune_fun.v1.vote.application.port.output.SaveVotePort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;


@Service
@UseCase
@RequiredArgsConstructor
public class RegisterVoteService implements RegisterVoteUseCase {

    private final LoadVotePort loadVotePort;
    private final SaveVotePort saveVotePort;
    private final SaveVotePaperVoteCountPort saveVotePaperVoteCountPort;

    @Override
    public void register(final Long votePaperId, final Long voteChoiceId, final User user) {
        if (loadVotePort.loadVoteByVoterAndVotePaperId(user.getUsername(), votePaperId).isPresent())
            throw CommonApplicationException.VOTE_POLICY_ONE_VOTE_PER_USER;

        saveVotePaperVoteCountPort.incrementVotePaperVoteCount(votePaperId, voteChoiceId);
        saveVotePort.saveVote(voteChoiceId, user.getUsername());
    }
}
