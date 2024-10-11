package gusev.dmitry.research.patterns.chainresp;

import gusev.dmitry.research.patterns.chainresp.authproviders.AuthenticationProvider;

public abstract class AuthenticationProcessor {

    public AuthenticationProcessor nextProcessor;

    // standard constructors
    public AuthenticationProcessor(AuthenticationProcessor nextProcessor) {
        this.nextProcessor = nextProcessor;
    }

    public abstract boolean isAuthorized(AuthenticationProvider authProvider);
}
