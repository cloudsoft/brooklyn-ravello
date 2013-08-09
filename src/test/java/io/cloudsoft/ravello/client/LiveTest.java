package io.cloudsoft.ravello.client;

import static com.google.common.base.Preconditions.checkNotNull;

import io.cloudsoft.ravello.api.RavelloApi;

public class LiveTest {

    private static final String USER_PROPERTY = "ravello.username";
    private static final String PASSWORD_PROPERTY = "ravello.password";

    final String endpoint = "https://cloud.ravellosystems.com/services";
    final String username;
    final String password;
    final RavelloApi ravello;

    LiveTest() {
        username = checkNotNull(System.getProperty(USER_PROPERTY),
                "Expect value for "+USER_PROPERTY+" system property");
        password = checkNotNull(System.getProperty(PASSWORD_PROPERTY),
                "Expect value for " + PASSWORD_PROPERTY + " system property");
        ravello = new RavelloApiImpl(endpoint, username, password);
    }

}
