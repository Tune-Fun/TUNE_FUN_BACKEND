package com.tune_fun.v1.vote.application.service;

import com.tune_fun.v1.common.exception.CommonApplicationException;
import com.tune_fun.v1.common.hexagon.UseCase;
import com.tune_fun.v1.vote.application.port.input.usecase.GetVotePaperUseCase;
import com.tune_fun.v1.vote.application.port.output.LoadVoteChoicePort;
import com.tune_fun.v1.vote.application.port.output.LoadVotePaperPort;
import com.tune_fun.v1.vote.domain.value.FullVotePaper;
import com.tune_fun.v1.vote.domain.value.RegisteredVoteChoice;
import com.tune_fun.v1.vote.domain.value.RegisteredVotePaper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.tune_fun.v1.common.response.MessageCode.VOTE_PAPER_NOT_FOUND;

@Service
@UseCase
@RequiredArgsConstructor
public class GetVotePaperService implements GetVotePaperUseCase {

    private final LoadVotePaperPort loadVotePaperPort;
    private final LoadVoteChoicePort loadVoteChoicePort;

    private final VoteValueMapper voteValueMapper;

    @Override
    public FullVotePaper getVotePaper(final Long votePaperId) {
        RegisteredVotePaper registeredVotePaper = loadVotePaperPort.loadRegisteredVotePaper(votePaperId)
                .orElseThrow(() -> new CommonApplicationException(VOTE_PAPER_NOT_FOUND));
        List<RegisteredVoteChoice> registeredVoteChoices = loadVoteChoicePort.loadRegisteredVoteChoice(votePaperId);

        return voteValueMapper.fullVotePaper(registeredVotePaper, registeredVoteChoices);
    }
}
