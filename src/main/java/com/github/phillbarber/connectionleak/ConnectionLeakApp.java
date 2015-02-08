package com.github.phillbarber.connectionleak;

import com.sun.jersey.api.client.Client;
import io.dropwizard.Application;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import java.net.URI;

public class ConnectionLeakApp extends Application<AppConfig> {


    public static final String USEFUL_SERVICE_HEALTH_CHECK = "useful-service-health-check";
    public static final String DEFAULT_CONFIG_FILE = "config-default.yml";
    public static final String CONNECTION_POOL_OF_SIZE_ONE_CONFIG_FILE = "config-with-connection-pool-of-size-one.yml";


    @Override
    public void initialize(Bootstrap<AppConfig> bootstrap) {  }


    @Override
    public void run(AppConfig appConfig, Environment environment) throws Exception {
        final Client client = new JerseyClientBuilder(environment).using(appConfig.getJerseyClientConfiguration())
                .build(AppConfig.USEFUL_SERVICE_HTTP_CLIENT);

        //to fix all acceptance tests, replace the newing up of the healthcheck with the following concrete class: UsefulServiceHealthCheckWithNoConnectionLeak
        environment.healthChecks().register(USEFUL_SERVICE_HEALTH_CHECK, new UsefulServiceHealthCheckWithConnectionLeak(client, new URI(AppConfig.USEFUL_SERVICE_VERSION_URI)));
        environment.jersey().register(new HelloWorldResource());
    }

    public static void main(String[] args) throws Exception{

        if (args == null || args.length == 0) {
            args = new String[]{"server", ResourceFileUtils.getFileFromClassPath(DEFAULT_CONFIG_FILE).getAbsolutePath()};
        }

        new ConnectionLeakApp().run(args);
    }
}
