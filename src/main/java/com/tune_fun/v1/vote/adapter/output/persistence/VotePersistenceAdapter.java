package com.tune_fun.v1.vote.adapter.output.persistence;

import com.tune_fun.v1.account.adapter.output.persistence.AccountJpaEntity;
import com.tune_fun.v1.account.adapter.output.persistence.AccountPersistenceAdapter;
import com.tune_fun.v1.common.constant.Constants;
import com.tune_fun.v1.common.stereotype.PersistenceAdapter;
import com.tune_fun.v1.common.util.StringUtil;
import com.tune_fun.v1.interaction.adapter.output.persistence.VotePaperLikeJpaEntity;
import com.tune_fun.v1.interaction.adapter.output.persistence.VotePaperLikeRepository;
import com.tune_fun.v1.interaction.application.port.output.DeleteLikePort;
import com.tune_fun.v1.interaction.application.port.output.LoadLikePort;
import com.tune_fun.v1.interaction.application.port.output.SaveLikePort;
import com.tune_fun.v1.vote.application.port.output.*;
import com.tune_fun.v1.vote.domain.behavior.SaveVoteChoice;
import com.tune_fun.v1.vote.domain.behavior.SaveVotePaper;
import com.tune_fun.v1.vote.domain.value.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.KeysetScrollPosition;
import org.springframework.data.domain.ScrollPosition;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Window;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static java.time.LocalDateTime.now;
import static java.util.stream.Collectors.toSet;
import static org.springframework.data.domain.ScrollPosition.forward;
import static org.springframework.data.domain.Sort.Order.desc;
import static org.springframework.data.domain.Sort.by;

@Component
@PersistenceAdapter
@RequiredArgsConstructor
public class VotePersistenceAdapter implements
        LoadVotePort, SaveVotePort,
        LoadVotePaperPort, SaveVotePaperPort, DeleteVotePaperPort,
        UpdateDeliveryAtPort, UpdateVideoUrlPort,
        LoadVoteChoicePort, SaveVoteChoicePort,
        LoadLikePort, SaveLikePort, DeleteLikePort,
        LoadVotePaperStatisticsPort, SaveVotePaperStatisticsPort {

    private final AccountPersistenceAdapter accountPersistenceAdapter;

    private final VoteRepository voteRepository;
    private final VotePaperRepository votePaperRepository;
    private final VoteChoiceRepository voteChoiceRepository;
    private final VotePaperLikeRepository votePaperLikeRepository;
    private final VotePaperStatisticsRepository votePaperStatisticsRepository;

    private final VotePaperMapper votePaperMapper;
    private final VoteChoiceMapper voteChoiceMapper;

    @Override
    public List<Long> loadVoterIdsByVotePaperUuid(final String uuid) {
        return voteRepository.findVoterIdsByVotePaperUuid(uuid);
    }

    @Override
    public Optional<RegisteredVote> loadVoteByVoterAndVotePaperId(final String voter, final Long votePaperId) {
        return voteRepository.findByVoterUsernameAndVotePaperId(voter, votePaperId);
    }

    @Override
    public void saveVote(final Long voteChoiceId, final String username) {
        VoteChoiceJpaEntity voteChoice = voteChoiceRepository.findById(voteChoiceId)
                .orElseThrow(() -> new IllegalArgumentException("VoteChoice not found"));

        AccountJpaEntity account = accountPersistenceAdapter.loadAccountByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));

        VoteJpaEntity vote = VoteJpaEntity.builder()
                .uuid(StringUtil.uuid())
                .voteChoice(voteChoice)
                .voter(account)
                .build();

        voteRepository.save(vote);
    }

    /**
     * TODO : VOTE_COUNT 정렬은 집계 로직 구현 후 추가 예정
     *
     * @param lastId   해당 페이지의 마지막 인덱스
     * @param sortType 정렬 방식
     * @param nickname 작성자 닉네임
     * @return {@link org.springframework.data.domain.Window} of {@link com.tune_fun.v1.vote.domain.value.ScrollableVotePaper}
     * @see <a href="https://github.com/spring-projects/spring-data-jpa/issues/2996">Keyset-scrolling queries add identifier columns twice when Sort already sorts by Id</a>
     * @see <a href="https://www.baeldung.com/spring-data-jpa-scroll-api">Spring Data JPA Scroll API</a><br>
     */
    @Override
    public Window<ScrollableVotePaper> scrollVotePaper(final Integer lastId, final String sortType, final String nickname) {
        KeysetScrollPosition position;

        if (lastId == null || lastId == 0) {
            position = ScrollPosition.keyset();
        }

        position = forward(Map.of("id", lastId, "voteEndAt", Constants.LOCAL_DATE_TIME_MIN));

        Sort sort = by(desc("id"), desc("voteEndAt"));
        Window<VotePaperJpaEntity> scroll = nickname != null ?
                votePaperRepository.findFirst10ByEnabledTrueAndAuthorNicknameContaining(nickname, position, sort) :
                votePaperRepository.findFirst10ByEnabledTrue(position, sort);

        Set<Long> votePaperIds = scroll.stream().map(VotePaperJpaEntity::getId).collect(toSet());
        Map<Long, Long> likeCountMap = votePaperStatisticsRepository.findLikeCountMap(votePaperIds);
        Map<Long, Long> voteCountMap = votePaperStatisticsRepository.findVoteCountMap(votePaperIds);

        return scroll.map(votePaperJpaEntity -> votePaperMapper.scrollableVotePaper(votePaperJpaEntity, likeCountMap.get(votePaperJpaEntity.getId()), voteCountMap.get(votePaperJpaEntity.getId())));
    }

    @Override
    public Optional<RegisteredVotePaper> loadRegisteredVotePaper(final String username) {
        return findProgressingVotePaperByAuthor(username).map(votePaperMapper::registeredVotePaper);
    }

    @Override
    public List<RegisteredVotePaper> loadRegisteredVotePapers(String username) {
        return findVotePapersByUsername(username).stream()
                .map(votePaperMapper::registeredVotePaper)
                .toList();
    }

    @Override
    public Optional<RegisteredVotePaper> loadRegisteredVotePaper(final Long votePaperId) {
        return findProgressingVotePaperById(votePaperId).map(votePaperMapper::registeredVotePaper);
    }

    @Override
    public RegisteredVotePaper saveVotePaper(final SaveVotePaper saveVotePaper) {
        AccountJpaEntity account = accountPersistenceAdapter.loadAccountByUsername(saveVotePaper.author())
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));

        VotePaperJpaEntity votePaper = votePaperMapper.fromSaveVotePaperBehavior(saveVotePaper, account);
        VotePaperJpaEntity savedVotePaper = votePaperRepository.save(votePaper);
        return votePaperMapper.registeredVotePaper(savedVotePaper);
    }

    @Override
    public void disableVotePaper(final Long votePaperId) {
        votePaperRepository.findById(votePaperId)
                .ifPresent(votePaper -> {
                    votePaper.disable();
                    votePaperRepository.save(votePaper);
                });
    }

    @Override
    public void disableVotePapers(final List<Long> votePaperIds) {
        List<VotePaperJpaEntity> votePaperJpaEntities = votePaperRepository.findAllById(votePaperIds);
        votePaperJpaEntities.forEach(VotePaperJpaEntity::disable);

        votePaperRepository.saveAll(votePaperJpaEntities);
    }

    @Override
    public RegisteredVotePaper updateDeliveryAt(final Long votePaperId, final LocalDateTime deliveryAt) {
        VotePaperJpaEntity votePaper = findOneAvailable(votePaperId, Constants.NULL_STRING)
                .orElseThrow(() -> new IllegalArgumentException("VotePaper not found"));

        VotePaperJpaEntity updatedVotePaper = votePaperMapper.updateDeliveryAt(deliveryAt, votePaper.toBuilder()).build();
        VotePaperJpaEntity savedVotePaper = votePaperRepository.save(updatedVotePaper);
        return votePaperMapper.registeredVotePaper(savedVotePaper);
    }

    @Override
    public RegisteredVotePaper updateVideoUrl(final Long votePaperId, final String videoUrl) {
        VotePaperJpaEntity votePaper = findOneAvailable(votePaperId, Constants.NULL_STRING)
                .orElseThrow(() -> new IllegalArgumentException("VotePaper not found"));

        VotePaperJpaEntity updatedVotePaper = votePaperMapper.updateVideoUrl(videoUrl, votePaper.toBuilder()).build();
        VotePaperJpaEntity savedVotePaper = votePaperRepository.save(updatedVotePaper);
        return votePaperMapper.registeredVotePaper(savedVotePaper);
    }

    @Override
    public List<RegisteredVoteChoice> loadRegisteredVoteChoice(Long votePaperId) {
        List<VoteChoiceJpaEntity> voteChoices = findAllByVotePaperId(votePaperId);
        return voteChoiceMapper.registeredVoteChoices(voteChoices);
    }

    @Override
    public Optional<RegisteredVoteChoice> loadVoteChoiceByUsername(Long votePaperId, String username) {
        return findByVotePaperIdAndUsername(votePaperId, username).map(voteChoiceMapper::registeredVoteChoice);
    }

    @Override
    public void saveVoteChoice(final Long votePaperId, final Set<SaveVoteChoice> behavior) {
        VotePaperJpaEntity votePaperJpaEntity = findOneAvailable(votePaperId, Constants.NULL_STRING)
                .orElseThrow(() -> new IllegalArgumentException("VotePaper not found"));

        Set<VoteChoiceJpaEntity> voteChoices = voteChoiceMapper.fromSaveVoteChoiceBehaviors(behavior);
        Set<VoteChoiceJpaEntity> updatedVoteChoices = voteChoices.stream()
                .map(voteChoice -> voteChoiceMapper.updateVoteChoice(votePaperJpaEntity, voteChoice.toBuilder()).build())
                .collect(toSet());

        voteChoiceRepository.saveAll(updatedVoteChoices);
    }

    @Override
    public Optional<RegisteredVotePaperLike> loadVotePaperLike(Long votePaperId, String username) {
        return votePaperLikeRepository.findByVotePaperIdAndLikerUsername(votePaperId, username);
    }

    @Override
    public void saveVotePaperLike(final Long votePaperId, final String username) {
        AccountJpaEntity account = accountPersistenceAdapter.loadAccountByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));

        VotePaperJpaEntity votePaper = votePaperRepository.findById(votePaperId)
                .orElseThrow(() -> new IllegalArgumentException("VotePaper not found"));

        VotePaperLikeJpaEntity votePaperLike = VotePaperLikeJpaEntity.builder()
                .votePaper(votePaper)
                .liker(account)
                .build();

        votePaperLikeRepository.save(votePaperLike);
    }

    @Override
    public void deleteVotePaperLike(final Long votePaperId, final String username) {
        votePaperLikeRepository.deleteByVotePaperIdAndLikerUsername(votePaperId, username);
    }

    @Override
    public void initializeStatistics(final Long votePaperId) {
        VotePaperStatisticsJpaEntity stat = new VotePaperStatisticsJpaEntity(votePaperId);
        votePaperStatisticsRepository.save(stat);
    }

    @Override
    public void updateLikeCount(final Long votePaperId, final Long likeCount) {
        votePaperStatisticsRepository.updateLikeCount(votePaperId, likeCount);
    }

    @Override
    public void updateVoteCount(final Long votePaperId, final Long voteCount) {
        votePaperStatisticsRepository.updateVoteCount(votePaperId, voteCount);
    }

    @Override
    public Long getLikeCount(final Long votePaperId) {
        VotePaperStatisticsJpaEntity stat = votePaperStatisticsRepository.findByVotePaperId(votePaperId)
                .orElseThrow(() -> new IllegalArgumentException("VotePaperStatistics not found"));
        return stat.getLikeCount();
    }

    @Override
    public Long getVoteCount(Long votePaperId) {
        VotePaperStatisticsJpaEntity stat = votePaperStatisticsRepository.findByVotePaperId(votePaperId)
                .orElseThrow(() -> new IllegalArgumentException("VotePaperStatistics not found"));
        return stat.getVoteCount();
    }

    public Optional<VotePaperJpaEntity> findOneAvailable(final Long votePaperId, final String username) {
        return votePaperRepository.findOneAvailable(votePaperId, username);
    }

    public Optional<VotePaperJpaEntity> findCompleteVotePaperById(final Long id) {
        return votePaperRepository.findByVoteEndAtBeforeAndIdAndEnabledTrue(now(), id);
    }

    public Optional<VotePaperJpaEntity> findProgressingVotePaperByAuthor(final String username) {
        return votePaperRepository.findByVoteEndAtAfterAndAuthorUsernameAndEnabledTrue(now(), username);
    }

    public List<VotePaperJpaEntity> findVotePapersByUsername(final String username) {
        return votePaperRepository.findAllByAuthorUsernameAndEnabledTrue(username);
    }

    public Optional<VotePaperJpaEntity> findProgressingVotePaperById(final Long id) {
        return votePaperRepository.findByVoteEndAtAfterAndIdAndEnabledTrue(now(), id);
    }

    public List<VoteChoiceJpaEntity> findAllByVotePaperId(final Long votePaperId) {
        return voteChoiceRepository.findAllByVotePaperId(votePaperId);
    }

    public Optional<VoteChoiceJpaEntity> findByVotePaperIdAndUsername(final Long votePaperId, final String username) {
        return voteChoiceRepository.findByVotePaperIdAndCreatedBy(votePaperId, username);
    }
}
