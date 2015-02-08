package com.github.phillbarber.connectionleak;

import com.github.tomakehurst.wiremock.junit.WireMockRule;

import javax.ws.rs.core.Response;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

public class StubbedUsefulService {

    public WireMockRule wireMockRule;

    public StubbedUsefulService(WireMockRule wireMockRule) {
        this.wireMockRule = wireMockRule;
    }

    public void addStubForVersionPageThatReturnsOK() {
        wireMockRule.stubFor(get(urlEqualTo("/version")).willReturn(aResponse().withStatus(Response.Status.OK.getStatusCode()).withBody("1.1")));
    }
}
