package gusev.dmitry.research.patterns.chainresp;

import gusev.dmitry.research.patterns.chainresp.authproviders.AuthenticationProvider;
import gusev.dmitry.research.patterns.chainresp.authproviders.OAuthTokenProvider;

public class OAuthProcessor extends AuthenticationProcessor {

    public OAuthProcessor(AuthenticationProcessor nextProcessor) {
        super(nextProcessor);
    }

    @Override
    public boolean isAuthorized(AuthenticationProvider authProvider) {
        if (authProvider instanceof OAuthTokenProvider) {
            return true;
        } else if (nextProcessor != null) {
            return nextProcessor.isAuthorized(authProvider);
        }

        return false;
    }
}