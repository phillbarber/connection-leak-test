package com.github.phillbarber.connectionleak;

import com.codahale.metrics.health.HealthCheck;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;

import javax.ws.rs.core.Response;
import java.net.URI;

public class UsefulServiceHealthCheckWithConnectionLeak extends HealthCheck {

    private Client usefulServiceClient;
    private URI usefulServiceStatusURI;

    public UsefulServiceHealthCheckWithConnectionLeak(Client usefulServiceClient, URI usefulServiceStatusURI) {
        this.usefulServiceClient = usefulServiceClient;
        this.usefulServiceStatusURI = usefulServiceStatusURI;
    }

    @Override
    protected Result check() {
        ClientResponse clientResponse = usefulServiceClient.resource(usefulServiceStatusURI).get(ClientResponse.class);
        if (clientResponse.getStatus() == Response.Status.OK.getStatusCode()){
            return Result.healthy();
        }
        return Result.unhealthy("Something wrong with useful service");
    }

}
