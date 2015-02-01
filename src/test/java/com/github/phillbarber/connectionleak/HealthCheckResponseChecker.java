package com.github.phillbarber.connectionleak;

import org.hamcrest.Matcher;

import static org.hamcrest.CoreMatchers.is;

public class HealthCheckResponseChecker {

    public static final String HEALTHY_MESSAGE = "{\"deadlocks\":{\"healthy\":true},\"useful-service-health-check\":{\"healthy\":true}}";


    public static Matcher<String> hasHealthyMessage() {
        return is(HEALTHY_MESSAGE);
    }
}
