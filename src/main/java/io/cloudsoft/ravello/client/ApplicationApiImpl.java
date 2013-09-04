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
        return ravelloClient.get(url).getList(ApplicationPropertiesDto.class);
    }

    @Override
    public ApplicationDto get(String id) {
        checkNotNull(id, "id");
        return ravelloClient.get(url + "/" + id).get(ApplicationDto.class);
    }

    @Override
    public ApplicationDto create(ApplicationDto application) {
        checkNotNull(application, "application");
        return ravelloClient.post(url, application).get(ApplicationDto.class);
    }

    @Override
    public ApplicationDto update(String id, ApplicationDto application) {
        return ravelloClient.put(url + "/" + checkNotNull(id, "id"), checkNotNull(application, "application"))
                .get(ApplicationDto.class);
    }

    @Override
    public void delete(String id) {
        checkNotNull(id, "id");
        ravelloClient.delete(url + "/" + id).consumeResponse();
    }

    @Override
    public boolean publish(String id, String preferredCloud, String preferredRegion) {
        checkNotNull(id, "id");
        checkNotNull(preferredCloud, "preferredCloud");
        checkNotNull(preferredRegion, "preferredRegion");
        if (!Cloud.fromString(preferredCloud).hasRegion(preferredRegion)) {
            LOG.warn("Publishing app {} to unknown region in {}: {}", id, preferredCloud, preferredRegion);
        }
        Map<String, String> body = ImmutableMap.of(
                "preferredCloud", preferredCloud.toUpperCase(),
                "preferredRegion", preferredRegion);
        return !ravelloClient.post(url + "/" + id + "/publish", body)
                .consumeResponse()
                .isErrorResponse();
    }

    @Override
    public boolean startVMs(String applicationId) {
        return runActionOnApplication(applicationId, "start");
    }

    @Override
    public boolean stopVMs(String applicationId) {
        return runActionOnApplication(applicationId, "stop");
    }

    @Override
    public boolean publishUpdates(String applicationId) {
        return runActionOnApplication(applicationId, "publishUpdates");
    }

    private boolean runActionOnApplication(String applicationId, String action) {
        return !ravelloClient.post(url + "/" + applicationId + "/" + action)
                .consumeResponse()
                .isErrorResponse();
    }

}
