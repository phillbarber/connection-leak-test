package com.github.phillbarber.connectionleak;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UnitTestThatMocksResponseObject {

    @Mock
    private Client client;

    @Mock
    private WebResource webResource;

    @Mock
    private ClientResponse clientResponse;

    private URI uri;


    @Before
    public void setUp() throws Exception {
        uri = new URI("");
        when(client.resource(uri)).thenReturn(webResource);
        when(webResource.get(ClientResponse.class)).thenReturn(clientResponse);
        when(clientResponse.getStatus()).thenReturn(Response.Status.OK.getStatusCode());

    }

    @Test
    public void givenUsefulServiceISOK_whenHealthCheckInvoked_thenIsHealthy() throws URISyntaxException {
        //To fix this test, replace this line with UsefulServiceHealthCheck check = new UsefulServiceHealthCheckWithNoConnectionLeak(client, uri);
        UsefulServiceHealthCheck check = new UsefulServiceHealthCheckWithConnectionLeak(client, uri);
        assertThat(check.check().isHealthy(), is(true));
        verify(clientResponse).close();
    }


}
