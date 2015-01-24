package com.github.phillbarber.connectionleak;


import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.io.Resources;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;
import java.net.URISyntaxException;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;

public class UsefulServiceTestWithRepeatsAcceptanceTest {

    @ClassRule
    public static final DropwizardAppRule appRule = new DropwizardAppRule<AppConfig>(ConnectionLeakApp.class, getAbsolutePath());
    private static final int SIZE_OF_CONNECTION_POOL = 1;


    @Rule
    //ToDo look into replacing this with a test that is org.junit.runners.Parameterized
    public RepeatRule repeatRule = new RepeatRule();

    private Client client = new Client();

    private static String getAbsolutePath()  {
        try {
            return new File(Resources.getResource("config-with-tiny-connection-pool.yml").toURI()).getAbsolutePath();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    @Repeat(times= SIZE_OF_CONNECTION_POOL+1)
    public void googleStatusPageReturns200Response(){
        ClientResponse clientResponse = googleStatusResource().get(ClientResponse.class);
        assertThat(clientResponse.getStatus(), equalTo(200));
    }

    @Test
    public void ensureConnectionPoolIsOfSize1(){
        assertThat(getMetricsResource().get(JsonNode.class)
                .get("gauges")
                .get("org.apache.http.conn.ClientConnectionManager." + AppConfig.USEFUL_SERVICE_HTTP_CLIENT + ".max-connections")
                .get("value").asInt(), equalTo(SIZE_OF_CONNECTION_POOL));

    }

    private WebResource googleStatusResource() {
        return client.resource("http://localhost:" + appRule.getLocalPort()).path(AppConfig.CONNECTION_LEAK_APP_USEFUL_SERVICE_URI);
    }

    private WebResource getMetricsResource() {
        return client.resource("http://localhost:" + appRule.getAdminPort()).path("/metrics");
    }


}
