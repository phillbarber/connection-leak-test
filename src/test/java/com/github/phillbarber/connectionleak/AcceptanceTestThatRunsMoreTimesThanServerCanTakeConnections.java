package com.github.phillbarber.connectionleak;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import javax.ws.rs.core.Response;

import static com.github.phillbarber.connectionleak.AppConfig.USEFUL_SERVICE_PORT;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class AcceptanceTestThatRunsMoreTimesThanServerCanTakeConnections {

    @ClassRule
    public static final DropwizardAppRule<AppConfig> appRule = new DropwizardAppRule<>(ConnectionLeakApp.class,
            ResourceFileUtils.getFileFromClassPath(ConnectionLeakApp.DEFAULT_CONFIG_FILE).getAbsolutePath());
    public static final int CONTAINER_THREADS = 2;

    @Rule
    //ToDo look into replacing this with a test that is org.junit.runners.Parameterized
    public RepeatRule repeatRule = new RepeatRule();

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().jettyAcceptors(1).jettyAcceptQueueSize(1).containerThreads(CONTAINER_THREADS).port(USEFUL_SERVICE_PORT));

    @Before
    public void setUp() throws Exception {
        new StubbedUsefulService(wireMockRule).addStubForVersionPage();
    }

    //The behaviour of jetty at this point confuses me. This test fails when we execute the same number of times as we
    //have jetty container thread, not container threads + 1.  There must be a thread in Jetty doing something else - perhaps
    //wiremock is doing something with the extra thread?
    @Test
    @Repeat(times=CONTAINER_THREADS)
    public void givenUsefulServiceIsOK_whenHealthCheckCalled_returnsHealthy(){
        ClientResponse clientResponse = getAdminResource(AppConfig.HEALTHCHECK_URI).get(ClientResponse.class);
        assertThat(clientResponse.getStatus(), equalTo(Response.Status.OK.getStatusCode()));
    }


    private WebResource getAdminResource(String resource) {
        return new Client().resource("http://localhost:" + appRule.getAdminPort()).path(resource);
    }
}
