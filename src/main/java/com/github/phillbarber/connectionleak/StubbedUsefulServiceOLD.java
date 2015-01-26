package com.github.phillbarber.connectionleak;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;

import javax.ws.rs.core.Response;

import java.net.URI;
import java.net.URISyntaxException;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

public class StubbedUsefulServiceOLD {

    private WireMockServer wireMockServer;
    private int port;

    public StubbedUsefulServiceOLD(int port) {
        wireMockServer = new WireMockServer(wireMockConfig().containerThreads(1000000).jettyAcceptors(1000).port(port));
        this.port = port;
    }

    public void startStubbedUsefulService() {
        wireMockServer.start();
        wireMockServer.stubFor(addStubForversionPageThatReturnsOK());
    }

    private MappingBuilder addStubForversionPageThatReturnsOK() {
        return get(urlEqualTo("/version")).willReturn(aResponse().withStatus(Response.Status.OK.getStatusCode()).withBody("1.1"));
    }

    public URI getVersionURL(){
        try {
            return new URI(String.format("http://localhost:%d/version", port));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

}
