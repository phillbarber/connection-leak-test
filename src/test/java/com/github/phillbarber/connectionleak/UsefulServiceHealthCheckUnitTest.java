package com.github.phillbarber.connectionleak;


import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;

import static com.github.phillbarber.connectionleak.AppConfig.USEFUL_SERVICE_PORT;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


public class UsefulServiceHealthCheckUnitTest {


    private final DefaultClientConfig cc = new DefaultClientConfig();

    private Client client = Client.create(cc);//This creates a jersey client instance which does not use a connection pool.  Think you're safe - you're wrong... the breaking point becomes something else...
    // ultimately you end up with the following exception:
    //(with wiremock set to following config.... containerThreads(1000000).jettyAcceptors(1000)....  java.net.SocketException: Too many open files: Too many open files at sun.nio.ch.ServerSocketChannelImpl.accept0(Native Method) ~[na:na]
    //OR (with wiremock set for 10000 threads) com.sun.jersey.api.client.ClientHandlerException: java.net.NoRouteToHostException: Cannot assign requested address

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(USEFUL_SERVICE_PORT);

    @Before
    public void setUp() throws Exception {
        new StubbedUsefulService(wireMockRule).addStubForVersionPage();
    }



    @Test
    public void googleStatusPageReturns200Response() throws URISyntaxException {

        UsefulServiceHealthCheck healthCheck =  new UsefulServiceHealthCheckWithConnectionLeak(client, new URI(AppConfig.USEFUL_SERVICE_VERSION_URI));  //failing test;

        int leasedConnectionsBefore = getLeasedConnections();
        for (int i = 0; i<100000; i++){
            if (i==287){
                System.out.println("UH OH");
            }
            System.out.println(i);
            assertThat(healthCheck.check().isHealthy(), is(true));
        }
        assertThat(leasedConnectionsBefore, is(getLeasedConnections()));
    }

    private int getLeasedConnections() {
        return 1;
    }


}
