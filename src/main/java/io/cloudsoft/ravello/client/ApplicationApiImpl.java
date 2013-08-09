package io.cloudsoft.ravello.client;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;

import io.cloudsoft.ravello.api.ApplicationApi;
import io.cloudsoft.ravello.dto.ApplicationDto;
import io.cloudsoft.ravello.dto.ApplicationDto.ApplicationPropertiesDto;
import io.cloudsoft.ravello.dto.Cloud;

public class ApplicationApiImpl implements ApplicationApi {

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationApiImpl.class);

    private final RavelloHttpClient ravelloClient;
    private final String url = "/applications";

    public ApplicationApiImpl(RavelloHttpClient httpClient) {
        this.ravelloClient = httpClient;
    }

    @Override
    public List<ApplicationPropertiesDto> get() {
        return ravelloClient.getList(url, ApplicationPropertiesDto.class);
    }

    @Override
    public ApplicationDto get(String id) {
        checkNotNull(id, "id");
        return ravelloClient.get(url + "/" + id, ApplicationDto.class);
    }

    @Override
    public ApplicationDto create(ApplicationDto application) {
        checkNotNull(application, "application");
        return ravelloClient.post(url, application, ApplicationDto.class);
    }

    @Override
    public void delete(String id) {
        checkNotNull(id, "id");
        ravelloClient.delete(url + "/" + id);
    }

    @Override
    public void publish(String id, String preferredCloud, String preferredRegion) {
        checkNotNull(id, "id");
        if (!Cloud.KNOWN_CLOUDS.contains(preferredCloud) ||
                !Cloud.fromValue(preferredCloud).hasRegion(preferredCloud)) {
            LOG.warn("Publishing app {} to unknown cloud and region: {}/{}", id, preferredCloud, preferredRegion);
        }
        Map<String, String> body = ImmutableMap.of(
                "preferredCloud", checkNotNull(preferredCloud),
                "preferredRegion", checkNotNull(preferredRegion));
        ravelloClient.post(url + "/" + id + "/publish", body);
    }

    @Override
    public void startVMs(String applicationId) {
        runActionOnApplication(applicationId, "start");
    }

    @Override
    public void stopVMs(String applicationId) {
        runActionOnApplication(applicationId, "stop");
    }

    @Override
    public void publishUpdates(String applicationId) {
        runActionOnApplication(applicationId, "publishUpdates");
    }

    private void runActionOnApplication(String applicationId, String action) {
        ravelloClient.post(url + "/" + applicationId + "/" + action);
    }

}
