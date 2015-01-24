package com.github.phillbarber.connectionleak;

import com.codahale.metrics.health.HealthCheck;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;

import javax.ws.rs.core.Response;

public class UsefulServiceHealthCheckWithConnectionLeak extends HealthCheck {

    private Client usefulServiceClient;
    private String usefulServiceStatusURI;

    public UsefulServiceHealthCheckWithConnectionLeak(Client usefulServiceClient, String usefulServiceStatusURI) {
        this.usefulServiceClient = usefulServiceClient;
        this.usefulServiceStatusURI = usefulServiceStatusURI;
    }

    @Override
    protected Result check() throws Exception {
        ClientResponse clientResponse = usefulServiceClient.resource(usefulServiceStatusURI).get(ClientResponse.class);
        if (clientResponse.getStatus() == Response.Status.OK.getStatusCode()){
            return Result.healthy();
        }
        return Result.unhealthy("Something wrong with useful service");
    }

}
