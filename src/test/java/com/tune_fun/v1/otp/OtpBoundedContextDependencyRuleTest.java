package com.tune_fun.v1.otp;

import com.tune_fun.v1.base.architecture.BoundedContextDependencyRuleTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class OtpBoundedContextDependencyRuleTest extends BoundedContextDependencyRuleTest {

    private static final String BOUNDED_CONTEXT_PACKAGE = "otp";

    @Override
    public String getBoundedContextPackage() {
        return BOUNDED_CONTEXT_PACKAGE;
    }

    @Test
    @DisplayName("Otp Bounded Context satisfied Hexagonal Architecture.")
    void checkOtpDependencyRule() {
        checkDependencyRule();
    }
}
