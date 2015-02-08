package com.github.phillbarber.connectionleak;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.sun.jersey.client.apache4.ApacheHttpClient4;
import com.sun.jersey.client.apache4.ApacheHttpClient4Handler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.net.URI;

import static com.github.phillbarber.connectionleak.AppConfig.USEFUL_SERVICE_PORT;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class IntegrationTestThatExaminesConnectionPoolBeforeAndAfterRun {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(USEFUL_SERVICE_PORT);

    private ApacheHttpClient4 client;
    private PoolingHttpClientConnectionManager poolingHttpClientConnectionManager;
    private UsefulServiceHealthCheck healthCheck;

    @Before
    public void setUp() throws Exception {
        poolingHttpClientConnectionManager = new PoolingHttpClientConnectionManager();
        client = new ApacheHttpClient4(new ApacheHttpClient4Handler(HttpClients.custom()
                .setConnectionManager(poolingHttpClientConnectionManager)
                .build(), null, false));
        //to fix all this test, replace the newing up of the healthcheck with the following concrete class: UsefulServiceHealthCheckWithNoConnectionLeak
        healthCheck = new UsefulServiceHealthCheckWithConnectionLeak(client, new URI(AppConfig.USEFUL_SERVICE_VERSION_URI));
    }

    @Test
    public void givenUsefulServiceIsOK_whenHealthCheckCalled_returnsHealthy() throws Exception{
        new StubbedUsefulService(wireMockRule).addStubForVersionPageThatReturnsOK();
        assertThat(healthCheck.check().isHealthy(), is(true));
    }

    @Test
    public void givenUsefulServicReturnsError_whenHealthCheckCalled_returnsNotHealthy() throws Exception{
        new StubbedUsefulService(wireMockRule).addStubForVersionPageThatReturnsError();
        assertThat(healthCheck.check().isHealthy(), is(false));
    }

    @After
    public void checkLeasedConnectionsIsZero() {
        assertThat(poolingHttpClientConnectionManager.getTotalStats().getLeased(), is(0));
    }
}
