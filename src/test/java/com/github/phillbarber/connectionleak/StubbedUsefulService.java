package com.github.phillbarber.connectionleak;

import com.github.tomakehurst.wiremock.junit.WireMockRule;

import javax.ws.rs.core.Response;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class StubbedUsefulService {

    public WireMockRule wireMockRule;

    public StubbedUsefulService(WireMockRule wireMockRule) {
        this.wireMockRule = wireMockRule;
    }

    public void addStubForVersionPageThatReturnsOK() {
        wireMockRule.stubFor(get(urlEqualTo("/version")).willReturn(aResponse().withStatus(Response.Status.OK.getStatusCode()).withBody("1.1")));
    }

    public void addStubForVersionPageThatReturnsError() {
        wireMockRule.stubFor(get(urlEqualTo("/version")).willReturn(aResponse().withStatus(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).withBody("SERVER ERROR")));
    }
}
