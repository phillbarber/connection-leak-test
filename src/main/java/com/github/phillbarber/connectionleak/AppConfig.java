package com.github.phillbarber.connectionleak;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.client.JerseyClientConfiguration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class AppConfig extends Configuration {


    public static final String USEFUL_SERVICE_HTTP_CLIENT = "useful-service-http-client";

    public static final String CONNECTION_LEAK_APP_HEALTHCHECK_URI = "/healthcheck";

    public static final int USEFUL_SERVICE_PORT = 7070;
    public static final String USEFUL_SERVICE_STATUS_URI = String.format("http://localhost:%d/status", USEFUL_SERVICE_PORT);

    @Valid
    @NotNull
    @JsonProperty
    private JerseyClientConfiguration jerseyClientConfiguration = new JerseyClientConfiguration();

    public JerseyClientConfiguration getJerseyClientConfiguration() {
        return jerseyClientConfiguration;
    }



}
