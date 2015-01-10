package com.github.phillbarber.connectionleak;


import com.google.common.io.Resources;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.setup.Environment;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.junit.ClassRule;
import org.junit.Test;

import java.io.File;
import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;

public class GoogleStatusResourceAcceptanceTest {

    @ClassRule
    public static final DropwizardAppRule appRule = new DropwizardAppRule<AppConfig>(ConnectionLeakApp.class, getAbsolutePath());
    public static final int ExXPECTED_STATUS_CODE_OF_PING_GOOGLE_TEST = 200;
    private Client client = new JerseyClientBuilder(appRule.getEnvironment()).build("");



    private static String getAbsolutePath()  {
        try {
            return new File(Resources.getResource("config.yml").toURI()).getAbsolutePath();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    public void testPingGoogle(){

        ClientResponse clientResponse = client.resource("http://localhost:" + appRule.getLocalPort() + "/google-status").get(ClientResponse.class);
        assertEquals(ExXPECTED_STATUS_CODE_OF_PING_GOOGLE_TEST, clientResponse.getStatus());
        assertEquals("Google is returning a 200 response", clientResponse.getEntity(String.class));
        clientResponse.close();
    }



}
