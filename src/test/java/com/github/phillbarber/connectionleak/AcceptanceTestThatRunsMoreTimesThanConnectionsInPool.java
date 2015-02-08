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

import static com.github.phillbarber.connectionleak.AppConfig.USEFUL_SERVICE_PORT;
import static com.github.phillbarber.connectionleak.HealthCheckResponseChecker.hasHealthyMessage;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class AcceptanceTestThatRunsMoreTimesThanConnectionsInPool {

    @ClassRule
    public static final DropwizardAppRule<AppConfig> appRule = new DropwizardAppRule<>(ConnectionLeakApp.class,
            ResourceFileUtils.getFileFromClassPath(ConnectionLeakApp.CONNECTION_POOL_OF_SIZE_ONE_CONFIG_FILE).getAbsolutePath());
    private static final int SIZE_OF_CONNECTION_POOL = 1;

    @Rule
    public RepeatRule repeatRule = new RepeatRule();

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(USEFUL_SERVICE_PORT);

    @Before
    public void setUp() throws Exception {
        new StubbedUsefulService(wireMockRule).addStubForVersionPageThatReturnsOK();
    }

    @Test
    @Repeat(times= SIZE_OF_CONNECTION_POOL+1)
    //this will fail with a org.apache.http.conn.ConnectionPoolTimeoutException, Timeout waiting for connection from pool
    public void givenUsefulServiceIsOK_whenHealthCheckCalled_returnsHealthy(){
        ClientResponse clientResponse = getAdminResource(AppConfig.HEALTHCHECK_URI).get(ClientResponse.class);
        assertThat(clientResponse.getEntity(String.class), hasHealthyMessage());

    }

    @Test
    public void givenApplicationStartedWithPoolSizeOne_whenMetricsViewed_ConnectionPoolIsOfSizeOne(){
        assertThat(getAdminResource("/metrics").get(JsonNode.class)
                .get("gauges")
                .get("org.apache.http.conn.ClientConnectionManager." + AppConfig.USEFUL_SERVICE_HTTP_CLIENT + ".max-connections")
                .get("value").asInt(), equalTo(SIZE_OF_CONNECTION_POOL));
    }


    private WebResource getAdminResource(String resource) {
        return new Client().resource("http://localhost:" + appRule.getAdminPort()).path(resource);
    }

}
