package com.tune_fun.v1.account.application.service;

import com.tune_fun.v1.account.application.port.input.usecase.FindArtistUseCase;
import com.tune_fun.v1.common.stereotype.UseCase;
import com.tune_fun.v1.interaction.domain.ArtistInfo;
import com.tune_fun.v1.interaction.domain.ScrollableArtist;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@UseCase
@RequiredArgsConstructor
public class FindArtistService implements FindArtistUseCase {

    @Override
    @Transactional(readOnly = true)
    public ArtistInfo findArtist(Long artistId) {
        return null;
    }
}
