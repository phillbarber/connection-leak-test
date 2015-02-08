package com.github.phillbarber.connectionleak;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.junit.*;

import static com.github.phillbarber.connectionleak.AppConfig.USEFUL_SERVICE_PORT;
import static com.github.phillbarber.connectionleak.HealthCheckResponseChecker.hasHealthyMessage;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class AcceptanceTestThatExaminesConnectionPoolBeforeAndAfterRun {


    @ClassRule
    public static final DropwizardAppRule<AppConfig> appRule = new DropwizardAppRule<>(ConnectionLeakApp.class,
            ResourceFileUtils.getFileFromClassPath(ConnectionLeakApp.DEFAULT_CONFIG_FILE).getAbsolutePath());

    @Rule
    public RepeatRule repeatRule = new RepeatRule();

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(USEFUL_SERVICE_PORT);


    private Client client = new Client();
    private int leasedConnectionsBefore;

    @Before
    public void setUp() throws Exception {
        new StubbedUsefulService(wireMockRule).addStubForVersionPageThatReturnsOK();
    }

    @Before
    public void storeLeasedConnectionsBeforeTestRun(){
        leasedConnectionsBefore = getLeasedConnections();
    }

    @After
    public void checkThatNumberOfLeasedConnectionsHaveNotChanged(){
        assertThat(leasedConnectionsBefore, equalTo(getLeasedConnections()));
    }

    @Test
    public void givenUsefulServiceIsOK_whenHealthCheckCalled_returnsHealthy(){
        ClientResponse clientResponse = connectionLeakAppHealthCheckResource().get(ClientResponse.class);
        assertThat(clientResponse.getEntity(String.class), hasHealthyMessage());
    }

    private int getLeasedConnections(){
        return client.resource("http://localhost:" + appRule.getAdminPort()).path("/metrics")
                .get(JsonNode.class)
                .get("gauges")
                .get("org.apache.http.conn.ClientConnectionManager." + AppConfig.USEFUL_SERVICE_HTTP_CLIENT + ".leased-connections")
                        .get("value").asInt();

    }


    private WebResource connectionLeakAppHealthCheckResource() {
        return client.resource("http://localhost:" + appRule.getAdminPort()).path(AppConfig.HEALTHCHECK_URI);
    }

}
