package com.github.phillbarber.connectionleak;


import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.client.apache4.ApacheHttpClient4;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;

import static com.github.phillbarber.connectionleak.AppConfig.USEFUL_SERVICE_PORT;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


public class UnitTestThatShowsHowDifferentClientsFail {


    public static final int LOTS_OF_TIMES = 100000;
    private final DefaultClientConfig cc = new DefaultClientConfig();

    //Allow lots of simultaneous ocnnections
    public static final int NUMBER_OF_CONTAINER_THREADS = 1000000;
    public static final int NUMBER_OF_JETTY_ACCEPTORS = 1000;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().jettyAcceptors(NUMBER_OF_JETTY_ACCEPTORS).containerThreads(NUMBER_OF_CONTAINER_THREADS).port(USEFUL_SERVICE_PORT));


    @Before
    public void setUp() throws Exception {
        new StubbedUsefulService(wireMockRule).addStubForVersionPage();
    }

    @Test
    //This test will fail on the 2nd attempt since the BasicClientConnectionManager does not allow more than one connection open at any point in time
    public void testWithApacheHttpClient4() throws URISyntaxException {
        testLotsOfTimes(new ApacheHttpClient4());
    }

    @Test
    @Ignore()//This test can take ages to run - disabled by default
    public void testWithStandardJerseyClient() throws URISyntaxException {

        /*
        The jersey client instance below does not use a connection pool.  In the absence of a pool, the breaking point
        then becomes either the server (not with the wiremock config above that allows many open connections) or the
        underlying OS eventually refusing to assign more connections (files) to the process and will/could give fail with
        a java.net.SocketException: Too many open files
        In my case this was after 12825 iterations
        I have also seen this fail with the following exception: java.net.NoRouteToHostException: Cannot assign requested address
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
