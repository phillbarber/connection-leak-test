package com.github.phillbarber.connectionleak;

import com.codahale.metrics.health.HealthCheck;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;

import javax.ws.rs.core.Response;


public class UsefulServiceHealthCheckWithNoConnectionLeak extends HealthCheck{

    private Client client;
    private String usefulServiceStatusURI;

    public UsefulServiceHealthCheckWithNoConnectionLeak(Client client, String usefulServiceStatusURI) {
        this.client = client;
        this.usefulServiceStatusURI = usefulServiceStatusURI;
    }


    @Override
    protected Result check() throws Exception {
        ClientResponse clientResponse = null;
        String result = "Unknown";

        try {
            clientResponse = client.resource(usefulServiceStatusURI).get(ClientResponse.class);
            if (clientResponse.getStatus() == Response.Status.OK.getStatusCode()){
                return Result.healthy();
            }
        }
        catch(Exception e){
            throw new RuntimeException(e);
        }
        finally{
            clientResponse.close();
        }
        return Result.unhealthy("Something wrong with useful service");
    }




}
