package com.github.phillbarber.connectionleak;

import com.codahale.metrics.health.HealthCheck;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class UsefulServiceHealthCheckUnitTest {


    private final DefaultClientConfig cc = new DefaultClientConfig();

    private Client client = Client.create(cc);//This creates a jersey client instance which does not use a connection pool.  Think you're safe - you're wrong... ultimately you end up with the following exception:
    //java.io.IOException: Too many open files
    //! at sun.nio.ch.ServerSocketChannelImpl.accept0(Native Method) ~[na:na]

    private UsefulServiceHealthCheckWithConnectionLeak healthCheck;
    //private GoogleStatusResourceWithNoConnectionLeak resource = new GoogleStatusResourceWithNoConnectionLeak(client, "http://www.google.co.uk"); //working test


    @Before
    public void setUp() throws Exception {
        healthCheck =  new UsefulServiceHealthCheckWithConnectionLeak(client, new URI("http://localhost:8081/healthcheck"));  //failing test;

    }

    @Test
    public void googleStatusPageReturns200Response(){

        //cc.

        int leasedConnectionsBefore = getLeasedConnections();
        for (int i = 0; i<100000; i++){
            System.out.println(i);
            assertThat(healthCheck.check().isHealthy(), is(HealthCheck.Result.healthy().isHealthy()));
        }
        //assertThat(resource.pingGoogle(), equalTo("Google is returning a 200 response"));
        assertThat(leasedConnectionsBefore, equalTo(getLeasedConnections()));
    }

    private int getLeasedConnections() {
        return 1;
    }


}
