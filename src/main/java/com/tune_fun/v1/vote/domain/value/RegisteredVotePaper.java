package com.tune_fun.v1.vote.domain.value;

import java.time.LocalDateTime;

public record RegisteredVotePaper(
        String id,
        String title,
        String content,
        String option,
        LocalDateTime voteStartAt,
        LocalDateTime voteEndAt,
        LocalDateTime deliveryAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}