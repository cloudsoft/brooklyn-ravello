package io.cloudsoft.ravello.api;

import java.util.List;

import io.cloudsoft.ravello.dto.ApplicationDto;
import io.cloudsoft.ravello.dto.ApplicationPropertiesDto;

public interface ApplicationApi {

    // Error handling?
    // 404 on get, return null. better way?

    public List<ApplicationPropertiesDto> get();

    public ApplicationDto get(String id);

    public ApplicationDto create(ApplicationDto application);

    public void delete(String id);

    public void publish(String id, String preferredCloud, String preferredRegion);

    // one for all actions or all actions as separate methods?
    //public void runActionOnPublishedApplication(String applicationId, String action);
    public void start(String applicationId);
    public void stop(String applicationId);

}
