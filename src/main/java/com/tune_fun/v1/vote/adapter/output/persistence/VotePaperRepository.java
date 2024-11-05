package com.tune_fun.v1.vote.adapter.output.persistence;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.KeysetScrollPosition;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Window;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VotePaperRepository extends JpaRepository<VotePaperJpaEntity, Long>, VotePaperCustomRepository {

    @EntityGraph(attributePaths = {"author"})
    Optional<VotePaperJpaEntity> findByVoteEndAtAfterAndAuthorUsernameAndEnabledTrue(LocalDateTime voteEndAt, String author);

    @EntityGraph(attributePaths = {"author"})
    Optional<VotePaperJpaEntity> findByVoteEndAtAfterAndIdAndEnabledTrue(LocalDateTime voteEndAt, Long id);

    Optional<VotePaperJpaEntity> findByVoteEndAtBeforeAndIdAndEnabledTrue(LocalDateTime voteEndAt, Long id);

    @EntityGraph(attributePaths = {"author"})
    Window<VotePaperJpaEntity> findFirst10ByEnabledTrueAndAuthorNicknameContaining(String nickname, KeysetScrollPosition position, Sort sort);

    @EntityGraph(attributePaths = {"author"})
    Window<VotePaperJpaEntity> findFirst10ByEnabledTrue(KeysetScrollPosition position, Sort sort);

    @EntityGraph(attributePaths = {"author"})
    List<VotePaperJpaEntity> findAllByAuthorUsernameAndEnabledTrue(String username);
}
