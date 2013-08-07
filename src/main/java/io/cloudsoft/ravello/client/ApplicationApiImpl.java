package io.cloudsoft.ravello.client;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

import io.cloudsoft.ravello.api.ApplicationApi;
import io.cloudsoft.ravello.dto.ApplicationDto;
import io.cloudsoft.ravello.dto.ApplicationPropertiesDto;

public class ApplicationApiImpl implements ApplicationApi {

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
        checkNotNull(id);
        return ravelloClient.get(url + "/" + id, ApplicationDto.class);
    }

    @Override
    public ApplicationDto create(ApplicationDto application) {
        checkNotNull(application);
        return ravelloClient.post(url, application, ApplicationDto.class);
    }

    @Override
    public void delete(String id) {
        checkNotNull(id);
        ravelloClient.delete(url + "/" + id);
    }

    @Override
    public void publish(String id, String preferredCloud, String preferredRegion) {
        checkNotNull(id);
        Map<String, String> body = ImmutableMap.of(
                "preferredCloud", checkNotNull(preferredCloud),
                "preferredRegion", checkNotNull(preferredRegion));
        ravelloClient.post(url + "/" + id + "/publish", body);
    }

    @Override
    public void start(String applicationId) {
        runActionOnApplication(applicationId, "start");
    }

    @Override
    public void stop(String applicationId) {
        runActionOnApplication(applicationId, "stop");
    }

    private void runActionOnApplication(String applicationId, String action) {
        ravelloClient.post(url + "/" + applicationId + "/" + action);
    }

}
