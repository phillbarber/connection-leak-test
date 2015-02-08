package com.github.phillbarber.connectionleak;

import org.hamcrest.Matcher;

import static org.hamcrest.CoreMatchers.is;

public class HealthCheckResponseChecker {

    public static final String HEALTHY_MESSAGE = "{\"deadlocks\":{\"healthy\":true},\"useful-service-health-check\":{\"healthy\":true}}";

    public static final String UNHEALTHY_MESSAGE = "{\"deadlocks\":{\"healthy\":true},\"useful-service-health-check\":{\"healthy\":false,\"message\":\"Something wrong with useful service\"}}";

    public static Matcher<String> hasHealthyMessage() {
        return is(HEALTHY_MESSAGE);
    }

    public static Matcher<String> hasUnHealthyMessage() {
        return is(UNHEALTHY_MESSAGE);
    }
}
