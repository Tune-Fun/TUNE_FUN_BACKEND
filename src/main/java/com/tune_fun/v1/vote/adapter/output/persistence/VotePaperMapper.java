package com.tune_fun.v1.vote.adapter.output.persistence;

import com.tune_fun.v1.account.adapter.output.persistence.AccountJpaEntity;
import com.tune_fun.v1.common.config.BaseMapperConfig;
import com.tune_fun.v1.common.util.StringUtil;
import com.tune_fun.v1.vote.domain.behavior.SaveVotePaper;
import com.tune_fun.v1.vote.domain.value.RegisteredVotePaper;
import com.tune_fun.v1.vote.domain.value.ScrollableVotePaper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Mapper(
        config = BaseMapperConfig.class,
        imports = StringUtil.class
)
public abstract class VotePaperMapper {

    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "author", source = "author.nickname")
    @Mapping(target = "authorUsername", source = "author.username")
    @Mapping(target = "profileImageUrl", source = "author.profileImageUrl")
    public abstract RegisteredVotePaper registeredVotePaper(final VotePaperJpaEntity votePaperJpaEntity);

    @Mapping(target = "authorUsername", source = "votePaperJpaEntity.author.username")
    @Mapping(target = "authorNickname", source = "votePaperJpaEntity.author.nickname")
    @Mapping(target = "remainDays", source = "votePaperJpaEntity", qualifiedByName = "remainDays")
    @Mapping(target = "totalVoteCount", source = "voteCount")
    @Mapping(target = "totalLikeCount", source = "likeCount")
    public abstract ScrollableVotePaper scrollableVotePaper(final VotePaperJpaEntity votePaperJpaEntity, final Long likeCount, final Long voteCount);

    @Named("remainDays")
    public Long remainDays(final VotePaperJpaEntity votePaperJpaEntity) {
        return LocalDateTime.now().until(votePaperJpaEntity.getVoteEndAt(), ChronoUnit.DAYS);
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "uuid", expression = "java(StringUtil.uuid())")
    @Mapping(target = "author", source = "author")
    @Mapping(target = "option", source = "saveVotePaper.option")
    @Mapping(target = "enabled", ignore = true)
    public abstract VotePaperJpaEntity fromSaveVotePaperBehavior(final SaveVotePaper saveVotePaper, final AccountJpaEntity author);

    @Mapping(target = "deliveryAt", source = "deliveryAt")
    public abstract VotePaperJpaEntity.VotePaperJpaEntityBuilder<?, ?> updateDeliveryAt(final LocalDateTime deliveryAt, @MappingTarget VotePaperJpaEntity.VotePaperJpaEntityBuilder<?, ?> builder);

    @Mapping(target = "videoUrl", source = "videoUrl")
    public abstract VotePaperJpaEntity.VotePaperJpaEntityBuilder<?, ?> updateVideoUrl(final String videoUrl, @MappingTarget VotePaperJpaEntity.VotePaperJpaEntityBuilder<?, ?> builder);
}
