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

import javax.ws.rs.core.Response;
import java.io.File;
import java.net.URISyntaxException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class UsefulServiceStatusTestWithBeforeAndAfterAssertionsAcceptanceTest {


    @ClassRule
    public static final DropwizardAppRule<AppConfig> appRule = new DropwizardAppRule<>(ConnectionLeakApp.class, getAbsolutePath());

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
    public void connectionLeakAppHealthCheckReturnsOK(){
        int leasedConnectionsBefore = getLeasedConnections();
        ClientResponse clientResponse = connectionLeakAppHealthCheckResource().get(ClientResponse.class);
        assertThat(clientResponse.getStatus(), equalTo(Response.Status.OK.getStatusCode()));
        assertThat(leasedConnectionsBefore, equalTo(getLeasedConnections()));
    }


    private int getLeasedConnections(){
        return client.resource("http://localhost:" + appRule.getAdminPort()).path("/metrics")
                .get(JsonNode.class)
                .get("gauges")
                .get("org.apache.http.conn.ClientConnectionManager." + AppConfig.USEFUL_SERVICE_HTTP_CLIENT + ".leased-connections")
                        .get("value").asInt();

    }

    private WebResource connectionLeakAppHealthCheckResource() {
        return client.resource("http://localhost:" + appRule.getAdminPort()).path(AppConfig.CONNECTION_LEAK_APP_HEALTHCHECK_URI);
    }


}
