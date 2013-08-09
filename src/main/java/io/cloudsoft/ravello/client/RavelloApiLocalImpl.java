package io.cloudsoft.ravello.client;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

import brooklyn.util.text.Identifiers;
import io.cloudsoft.ravello.api.ApplicationApi;
import io.cloudsoft.ravello.api.RavelloApi;
import io.cloudsoft.ravello.dto.ApplicationDto;
import io.cloudsoft.ravello.dto.ApplicationDto.ApplicationPropertiesDto;
import io.cloudsoft.ravello.dto.NetworkConnectionDto;
import io.cloudsoft.ravello.dto.VmDto;
import io.cloudsoft.ravello.dto.VmRuntimeInformationDto;

public class RavelloApiLocalImpl implements RavelloApi {

    private static final Logger LOG = LoggerFactory.getLogger(RavelloApiLocalImpl.class);

    @Override
    public ApplicationApi getApplicationApi() {
        return new LocalApplicationApiImpl();
    }

    public static class LocalApplicationApiImpl implements ApplicationApi {

        private static Map<String, ApplicationDto> applications = Maps.newHashMap();

        private static final NetworkConnectionDto LOCALHOST_NETWORK_DTO = NetworkConnectionDto.builder()
                .build();

        @Override
        public List<ApplicationPropertiesDto> get() {
            ImmutableList.Builder<ApplicationPropertiesDto> properties = ImmutableList.builder();
            for (ApplicationDto app : applications.values()) {
                properties.add(app.getProperties());
            }
            return properties.build();
        }

        @Override
        public ApplicationDto get(String id) {
            return applications.get(id);
        }

        @Override
        public ApplicationDto create(ApplicationDto application) {
            String id = Identifiers.makeRandomId(8);
            ApplicationDto created = application.toBuilder()
                    .id(id)
                    .build();
            applications.put(id, created);
            return created;
        }

        @Override
        public ApplicationDto update(String id, ApplicationDto application) {
            checkArgument(applications.containsKey(id));
            checkArgument(application.getVersion() > applications.get(id).getVersion());

            // Modify all VMs in application to point at localhost
            ApplicationDto.Builder modifiedApp = application.toBuilder().vms();
            for (VmDto vm : application.getVMs()) {
                VmDto localhostVM = vm.toBuilder()
                        .runtimeInformation(new VmRuntimeInformationDto(false, "localhost", "PUBLISHED"))
                        .build();
                modifiedApp.addVm(localhostVM);
            }

            ApplicationDto updatedApplication = modifiedApp.build();
            applications.put(id, updatedApplication);
            return updatedApplication;
        }

        @Override
        public void delete(String id) {
            applications.remove(id);
        }

        @Override
        public void publish(String id, String preferredCloud, String preferredRegion) {
            LOG.info("(published {} to {}:{})", id, preferredCloud, preferredRegion);
        }

        @Override
        public void startVMs(String applicationId) {
            LOG.info("(started all VMs)");
        }

        @Override
        public void stopVMs(String applicationId) {
            LOG.info("(stopped all VMs)");
        }

        @Override
        public void publishUpdates(String applicationId) {
            LOG.info("(published updates)");
        }
    }
}
