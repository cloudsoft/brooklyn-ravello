package io.cloudsoft.ravello.client;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import joptsimple.internal.Strings;

public class HttpResponseWrapperTest extends LiveTest {

    RavelloHttpClient client;

    @BeforeMethod(alwaysRun = true)
    public void initialiseClient() {
        client = new RavelloHttpClient(endpoint, getUsername(), getPassword());
    }

    @Test(groups = "live")
    public void testIsErrorTrueAfterRequestForUnknownApplication() {
        HttpResponseWrapper response = client.get("/applications/35225605");
        assertTrue(response.isErrorResponse(), "Expected request for nonexistant application to be an error");
    }

    // TODO: This test fails if tests are run with anything but cloudsoft email address

    @Test(groups = "live")
    public void testErrorMessageExtractedFromHeader() {
        HttpResponseWrapper response = client.get("/applications/35225605");
        assertFalse(Strings.isNullOrEmpty(response.getErrorMessage()), "Expected error message in response to invalid application");
    }

}
