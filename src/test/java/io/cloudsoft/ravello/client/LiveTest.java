package io.cloudsoft.ravello.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;

import brooklyn.config.BrooklynProperties;
import io.cloudsoft.ravello.api.RavelloApi;

public class LiveTest {

    private static final Logger LOG = LoggerFactory.getLogger(LiveTest.class);

    protected final BrooklynProperties properties = BrooklynProperties.Factory.newDefault();
    protected final String endpoint = "https://cloud.ravellosystems.com/services";
    protected final RavelloApi ravello;

    /** Looks for Ravello credentials in brooklyn.properties, then in system properties. */
    public LiveTest() {
        Optional<String> username = Optional.fromNullable(getUsername());
        Optional<String> password = Optional.fromNullable(getPassword());

        if (!username.isPresent() || !password.isPresent()) {
            LOG.info("Didn't find Ravello credentials in brooklyn properties, looking in system properties instead.");
            username = Optional.fromNullable(System.getProperty("ravello.username"));
            password = Optional.fromNullable(System.getProperty("ravello.password"));
        } else {
            LOG.info("Found Ravello credentials in brooklyn properties");
        }

        if (!username.isPresent() || !password.isPresent()) {
            throw new RuntimeException("Expect Ravello username and password to be given in brooklyn or system properties");
        }

        ravello = new RavelloApiImpl(endpoint, username.get(), password.get());
    }

    protected String getUsername() {
        return properties.getFirst("brooklyn.location.named.ravello.username");
    }

    protected String getPassword() {
        return properties.getFirst("brooklyn.location.named.ravello.password");
    }
}
