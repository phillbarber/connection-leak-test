package com.github.phillbarber.connectionleak;

import com.codahale.metrics.health.HealthCheck;

public interface UsefulServiceHealthCheck {


    public HealthCheck.Result check();
}
