package com.github.phillbarber.connectionleak;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.sun.jersey.client.apache4.ApacheHttpClient4;
import com.sun.jersey.client.apache4.ApacheHttpClient4Handler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
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
    private PoolingHttpClientConnectionManager poolingHttpCLientCOnnectionManager = new PoolingHttpClientConnectionManager();;

    @Before
    public void setUp() throws Exception {
        new StubbedUsefulService(wireMockRule).addStubForVersionPageThatReturnsOK();

        client = new ApacheHttpClient4(new ApacheHttpClient4Handler(HttpClients.custom()
                .setConnectionManager(poolingHttpCLientCOnnectionManager)
                .build(), null, false));
    }


    @Test
    public void givenUsefulServiceIsOK_whenHealthCheckCalled_returnsHealthy() throws Exception{
        UsefulServiceHealthCheck healthCheck =
                new UsefulServiceHealthCheckWithConnectionLeak(client, new URI(AppConfig.USEFUL_SERVICE_VERSION_URI));

        assertThat(healthCheck.check().isHealthy(), is(true));
        assertThat(poolingHttpCLientCOnnectionManager.getTotalStats().getLeased(), is(0));
    }
}
