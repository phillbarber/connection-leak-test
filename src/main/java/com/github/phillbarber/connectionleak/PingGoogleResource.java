package com.github.phillbarber.connectionleak;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

@Path("/ping-google")
public class PingGoogleResource {


    @GET
    public String pingGoogle() {
        return "hi";
    }



}
