package io.cloudsoft.ravello.client;

import io.cloudsoft.ravello.api.RavelloApi;

public class LiveTest {

    final String endpoint = "https://cloud.ravellosystems.com/services";
    final String username;
    final String password;
    final RavelloApi ravello;

    LiveTest() {
        username = System.getProperty("ravello.user");
        password = System.getProperty("ravello.password");
        ravello = new RavelloApiImpl(endpoint, username, password);
    }

}
