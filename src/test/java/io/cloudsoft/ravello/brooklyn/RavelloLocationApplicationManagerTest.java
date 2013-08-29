package io.cloudsoft.ravello.brooklyn;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import io.cloudsoft.ravello.api.ApplicationApi;
import io.cloudsoft.ravello.api.RavelloApi;
import io.cloudsoft.ravello.dto.ApplicationDto;
import io.cloudsoft.ravello.dto.VmDto;
import joptsimple.internal.Strings;

public class RavelloLocationApplicationManagerTest {

    RavelloApi ravelloApi;
    ApplicationApi applicationApi;
    RavelloLocationApplicationManager manager;

    final String mockAppId = "appId";
    final ApplicationDto createdApp = ApplicationDto.builder()
            .name("mock-app-dto")
            .id(mockAppId)
            .build();
    ApplicationDto lastUpdatedAppDto = createdApp;

    @BeforeMethod
    public void setupMocks() {
        ravelloApi = mock(RavelloApi.class);
        applicationApi = mock(ApplicationApi.class);
        manager = new RavelloLocationApplicationManager(ravelloApi, "");

        when(ravelloApi.getApplicationApi()).thenReturn(applicationApi);
        when(applicationApi.create(any(ApplicationDto.class))).thenReturn(createdApp);
        when(applicationApi.update(eq(mockAppId), any(ApplicationDto.class))).thenAnswer(new Answer<ApplicationDto>() {
            @Override
            public ApplicationDto answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                String updateId = String.class.cast(args[0]);
                ApplicationDto updatedApp = ApplicationDto.class.cast(args[1]);
                assertEquals(updateId, mockAppId);

                // VMs need IDs
                List<VmDto> vms = Lists.transform(updatedApp.getVMs(), new Function<VmDto, VmDto>() {
                    @Override
                    public VmDto apply(VmDto input) {
                        return !Strings.isNullOrEmpty(input.getId()) ? input : input.toBuilder().id(UUID.randomUUID().toString()).build();
                    }
                });
                lastUpdatedAppDto = updatedApp.toBuilder().vms(vms).build();
                return lastUpdatedAppDto;
            }
        });
        when(applicationApi.get(mockAppId)).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return lastUpdatedAppDto;
            }
        });
        // Alter updated DTO to have published true.
        Answer<Void> answerSettingLastUpdatedAppToPublished = new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                lastUpdatedAppDto = lastUpdatedAppDto.toBuilder().published(true).build();
                return null;
            }
        };
        doAnswer(answerSettingLastUpdatedAppToPublished)
                .when(applicationApi).publish(anyString(), anyString(), anyString());
    }

    @Test
    public void testCreateNewPublishedVM() {
        manager.createNewPublishedVM(Collections.emptyList());

        verify(applicationApi).create(any(ApplicationDto.class));
        verify(applicationApi).update(eq(mockAppId), any(ApplicationDto.class));
        verify(applicationApi).publish(eq(mockAppId), anyString(), anyString());
        verify(applicationApi).get(eq(mockAppId));

        verifyNoMoreInteractions(applicationApi);
    }

    @Test
    public void testReleaseVM() {
        manager.createNewPublishedVM(Collections.emptyList());
        VmDto created = manager.createNewPublishedVM(Collections.emptyList());
        manager.release(created);

        // One publish for first vm, two publish updates for second vm and release.
        verify(applicationApi).publish(eq(mockAppId), anyString(), anyString());
        verify(applicationApi, times(2)).publishUpdates(mockAppId);
        verify(applicationApi, times(3)).update(eq(mockAppId), any(ApplicationDto.class));
    }

    @Test
    public void testApplicationDeletedWhenReleaseCalledWithOneVMRemaining() {
        VmDto created = manager.createNewPublishedVM(Collections.emptyList());
        manager.release(created);
        verify(applicationApi).delete(mockAppId);
    }

    @Test
    public void testDeleteApplication() {
        manager.createNewPublishedVM(Collections.emptyList());
        manager.deleteApplicationModel();
        verify(applicationApi).delete(mockAppId);
    }

    @Test
    public void testNPublishesForNNewVMs() {
        // Initialise one VM
        manager.createNewPublishedVM(Collections.emptyList());

        // Create several VMs at once
        List<Thread> threads = ImmutableList.of(
                newThreadCreatingPublishedVM(),
                newThreadCreatingPublishedVM(),
                newThreadCreatingPublishedVM());
        for (Thread t : threads) t.start();
        for (Thread t : threads) {
            try { t.join(); } catch (InterruptedException e) { }
        }

        verify(applicationApi).publish(eq(mockAppId), anyString(), anyString());
        verify(applicationApi, times(threads.size())).publishUpdates(eq(mockAppId));
    }

    private Thread newThreadCreatingPublishedVM() {
        final RavelloLocationApplicationManager manager = this.manager;
        return new Thread() {
            public void run() {
                manager.createNewPublishedVM(Collections.emptyList());
            }
        };
    }
}
