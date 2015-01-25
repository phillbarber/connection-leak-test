package com.github.phillbarber.connectionleak;

import com.google.common.io.Resources;
import com.sun.jersey.api.client.Client;
import io.dropwizard.Application;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import javax.ws.rs.Path;
import java.io.File;

public class ConnectionLeakApp extends Application<AppConfig> {


    public static final String USEFUL_SERVICE_HEALTH_CHECK = "useful-service-health-check";
    private StubbedUsefulService stubbedUsefulService;

    @Override
    public void initialize(Bootstrap<AppConfig> bootstrap) {  }


    @Override
    public void run(AppConfig appConfig, Environment environment) throws Exception {

        //start a stubbed useful service with wiremock
        stubbedUsefulService = new StubbedUsefulService(AppConfig.USEFUL_SERVICE_PORT);
        stubbedUsefulService.startStubbedUsefulService();

        final Client client = new JerseyClientBuilder(environment).using(appConfig.getJerseyClientConfiguration())
                .build(AppConfig.USEFUL_SERVICE_HTTP_CLIENT);

        //environment.healthChecks().register(USEFUL_SERVICE_HEALTH_CHECK, new UsefulServiceHealthCheckWithConnectionLeak(client, stubbedUsefulService.getVersionURL()));
        environment.healthChecks().register(USEFUL_SERVICE_HEALTH_CHECK, new UsefulServiceHealthCheckWithNoConnectionLeak(client, stubbedUsefulService.getVersionURL()));

        environment.jersey().register(new HelloWorldResource());

    }



    public static void main(String[] args) throws Exception{

        if (args == null || args.length == 0) {
            args = new String[]{"server", new File(Resources.getResource("config-default.yml").toURI()).getAbsolutePath()};
        }

        new ConnectionLeakApp().run(args);
    }


}
