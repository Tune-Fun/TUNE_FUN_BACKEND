package com.tune_fun.v1.vote.application.port.output;

import com.tune_fun.v1.vote.domain.event.VotePaperRegisterEvent;

public interface ProduceVotePaperUploadEventPort {

    void produceVotePaperUploadEvent(final VotePaperRegisterEvent votePaperRegisterEvent);

}
