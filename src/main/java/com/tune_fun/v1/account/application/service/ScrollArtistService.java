package com.tune_fun.v1.account.application.service;

import com.tune_fun.v1.account.application.port.output.LoadAccountPort;
import com.tune_fun.v1.common.stereotype.UseCase;
import com.tune_fun.v1.account.application.port.input.usecase.ScrollArtistUseCase;
import com.tune_fun.v1.interaction.domain.ScrollableArtist;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Window;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
public class ScrollArtistService implements ScrollArtistUseCase {

    private final LoadAccountPort loadAccountPort;

    @Transactional(readOnly = true)
    @Override
    public Window<ScrollableArtist> scrollArtist(final Integer lastId, final String nickname) {
        loadAccountPort.scrollArtist(lastId, nickname);
        return null;
    }

}
