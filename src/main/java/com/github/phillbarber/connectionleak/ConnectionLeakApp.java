package com.github.phillbarber.connectionleak;

import com.google.common.io.Resources;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import java.io.File;

public class ConnectionLeakApp extends Application<AppConfig> {


    @Override
    public void initialize(Bootstrap<AppConfig> bootstrap) {

    }

    @Override
    public void run(AppConfig appConfig, Environment environment) throws Exception {
        environment.jersey().register(new PingGoogleResource());
    }

    public static void main(String[] args) throws Exception{
        if (args == null || args.length == 0) {
            args = new String[]{"server", new File(Resources.getResource("config.yml").toURI()).getAbsolutePath()};
        }

        new ConnectionLeakApp().run(args);
    }
}
