package com.github.phillbarber.connectionleak;


import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.sun.jersey.api.client.Client;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;

import static com.github.phillbarber.connectionleak.AppConfig.USEFUL_SERVICE_PORT;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;


@Ignore("Disabled by default as it can take ages to run.  Not recommended for your test suite, but interesting nonetheless.")
public class UnitTestThatShowsUnderlyingOSRefusingToAllocateMoreConnections
{


    public static final int LOTS_OF_TIMES = 100000;

    //Allow lots of simultaneous connections
    public static final int NUMBER_OF_CONTAINER_THREADS = 1000000;
    public static final int NUMBER_OF_JETTY_ACCEPTORS = 1000;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().jettyAcceptors(NUMBER_OF_JETTY_ACCEPTORS).containerThreads(NUMBER_OF_CONTAINER_THREADS).port(USEFUL_SERVICE_PORT));


    @Before
    public void setUp() throws Exception {
        new StubbedUsefulService(wireMockRule).addStubForVersionPage();
    }

    @Test
    public void testWithStandardJerseyClient() throws URISyntaxException {

        /*
        The jersey client instance below does not use a connection pool.  In the absence of a pool, the breaking point
        then becomes either the server or the underlying OS on the client refusing to assign too many connections to one
        process.  In this example we have configured WireMock to allow many open connections so the client eventually
        fails with a java.net.SocketException: Too many open files
        In my case this was after 12825 iterations
        I'm sure that I have also seen this fail with: java.net.NoRouteToHostException: Cannot assign requested address
         */

        testLotsOfTimes(Client.create());
    }


    private void testLotsOfTimes(Client client) throws URISyntaxException {
        int currentIteration = 0;
        try {
            UsefulServiceHealthCheck healthCheck = new UsefulServiceHealthCheckWithConnectionLeak(client, new URI(AppConfig.USEFUL_SERVICE_VERSION_URI));
            for (; currentIteration < LOTS_OF_TIMES; currentIteration++) {
                if (!healthCheck.check().isHealthy()) {
                    throw new RuntimeException(getErrorMessage(currentIteration) + " healthcheck returned false");
                }
            }
        }
        catch(Exception e){
            throw new RuntimeException(getErrorMessage(currentIteration), e);
        }
    }

    private String getErrorMessage(int currentIteration) {
        return String.format("Blew up after %d iteration(s)", currentIteration);
    }

}
