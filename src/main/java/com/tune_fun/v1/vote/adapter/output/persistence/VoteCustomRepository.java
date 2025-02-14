package com.tune_fun.v1.vote.adapter.output.persistence;

import com.tune_fun.v1.vote.domain.value.RegisteredVote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface VoteCustomRepository {

    List<Long> findVoterIdsByVotePaperUuid(final String uuid);

    Optional<RegisteredVote> findByVoterUsernameAndVotePaperId(final String voter, final Long votePaperId);

    Map<Long, Long> countVotesByVotePaperIds(final List<Long> votePaperIds);

}