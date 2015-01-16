package com.github.phillbarber.connectionleak;


import com.google.common.io.Resources;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;
import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;

public class GoogleStatusResourceAcceptanceTest {

    @ClassRule
    public static final DropwizardAppRule appRule = new DropwizardAppRule<AppConfig>(ConnectionLeakApp.class, getAbsolutePath());

    @Rule
    public RepeatRule repeatRule = new RepeatRule();

    private static String getAbsolutePath()  {
        try {
            return new File(Resources.getResource("config-unit-test.yml").toURI()).getAbsolutePath();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    @Repeat(times=2)
    public void testPingGoogle(){
        ClientResponse clientResponse = new Client().resource("http://localhost:" + appRule.getLocalPort()).path("/google-status").get(ClientResponse.class);
        assertEquals("Google is returning a 200 response", clientResponse.getEntity(String.class));
        clientResponse.close();
    }

}
