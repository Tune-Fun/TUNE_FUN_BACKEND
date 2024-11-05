package com.tune_fun.v1.vote.application.port.output;

import java.util.List;

public interface DeleteVotePaperPort {

    void disableVotePaper(final Long votePaperId);

    void disableVotePapers(final List<Long> votePaperIds);
}
