package com.github.phillbarber.connectionleak;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/google-status")
public class GoogleStatusResourceWithNoConnectionLeak  {

    private Client client;

    public GoogleStatusResourceWithNoConnectionLeak(Client client) {
        this.client = client;
    }

    @GET
    public String pingGoogle() {

        ClientResponse clientResponse = null;
        String result = "Failed";

        try {
            clientResponse = client.resource("http://www.google.com").get(ClientResponse.class);
            result = String.format("Google is returning a %d response", clientResponse.getStatus());
        }
        catch(Exception e){
            throw new RuntimeException(e);
        }
        finally{
            clientResponse.close();
        }

        return result;
    }



}
