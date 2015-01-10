package com.github.phillbarber.connectionleak;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/google-status")
public class GoogleStatusResourceWithAConnectionLeak {

    private Client client;

    public GoogleStatusResourceWithAConnectionLeak(Client client) {
        this.client = client;
    }

    @GET
    public String pingGoogle() {
        ClientResponse clientResponse = client.resource("http://www.google.com").get(ClientResponse.class);
        return String.format("Google is returning a %d response", clientResponse.getStatus());
    }



}
