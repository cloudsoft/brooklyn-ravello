package io.cloudsoft.ravello.client;

import io.cloudsoft.ravello.api.ApplicationApi;
import io.cloudsoft.ravello.api.RavelloApi;

public class RavelloApiImpl implements RavelloApi {

    private final RavelloHttpClient ravelloClient;

    public RavelloApiImpl(String endpoint, String username, String password) {
        ravelloClient = new RavelloHttpClient(endpoint, username, password);
    }

    @Override
    public ApplicationApi getApplicationApi() {
        return new ApplicationApiImpl(ravelloClient);
    }
}
