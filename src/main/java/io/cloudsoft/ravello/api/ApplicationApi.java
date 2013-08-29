package io.cloudsoft.ravello.api;

import java.util.List;

import io.cloudsoft.ravello.dto.ApplicationDto;
import io.cloudsoft.ravello.dto.ApplicationDto.ApplicationPropertiesDto;

public interface ApplicationApi {

    // Error handling?
    // 404 on get, return null. better way? Could return Optional

    public List<ApplicationPropertiesDto> get();
    public ApplicationDto get(String id);
    public ApplicationDto create(ApplicationDto application);
    public ApplicationDto update(String id, ApplicationDto application);
    public void delete(String id);

    public boolean publish(String id, String preferredCloud, String preferredRegion);
    public boolean startVMs(String applicationId);
    public boolean stopVMs(String applicationId);
    public boolean publishUpdates(String applicationId);

}
