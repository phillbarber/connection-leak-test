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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class GoogleStatusTestWithBeforeAndAfterConnectionPoolAssertions {


    @ClassRule
    public static final DropwizardAppRule appRule = new DropwizardAppRule<AppConfig>(ConnectionLeakApp.class, getAbsolutePath());
    private static final int SIZE_OF_CONNECTION_POOL = 1;

    @Rule
    public RepeatRule repeatRule = new RepeatRule();

    private Client client = new Client();

    private static String getAbsolutePath()  {
        try {
            //can load default production config here
            return new File(Resources.getResource("config-default.yml").toURI()).getAbsolutePath();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void googleStatusPageReturns200Response(){
        int leasedConnectionsBefore = getLeasedConnections();
        ClientResponse clientResponse = googleStatusResource().get(ClientResponse.class);
        assertThat(clientResponse.getStatus(), equalTo(200));
        assertThat(leasedConnectionsBefore, equalTo(getLeasedConnections()));
    }


    //ToDo does this cause an issue if there are two connections pools?
    private int getLeasedConnections(){
        return getMetricsResource().get(JsonNode.class)
                .get("gauges")
                .get("org.apache.http.conn.ClientConnectionManager.google-resource-http-client.leased-connections")
                .get("value").asInt();

    }

    private WebResource googleStatusResource() {
        return client.resource("http://localhost:" + appRule.getLocalPort()).path("/google-status");
    }

    private WebResource getMetricsResource() {
        return client.resource("http://localhost:" + appRule.getAdminPort()).path("/metrics");
    }

}
