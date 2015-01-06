package com.github.phillbarber.connectionleak;


import com.google.common.io.Resources;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.setup.Environment;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import java.io.File;
import java.net.URISyntaxException;

public class PingGoogleResourceTest {

    @ClassRule
    public static final DropwizardAppRule appRule = new DropwizardAppRule<AppConfig>(ConnectionLeakApp.class, getAbsolutePath());


    private static String getAbsolutePath()  {
        try {
            return new File(Resources.getResource("config.yml").toURI()).getAbsolutePath();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    public void testPingGoogle(){
        Environment environment = appRule.getEnvironment();
        Client client = new JerseyClientBuilder(environment).build("");
        ClientResponse clientResponse = client.resource("http://localhost:" + appRule.getLocalPort() + "/ping-google").get(ClientResponse.class);
        Assert.assertEquals(200, clientResponse.getStatus());
        String responseAsString = clientResponse.getEntity(String.class);
        appRule.getLocalPort();
    }

}
