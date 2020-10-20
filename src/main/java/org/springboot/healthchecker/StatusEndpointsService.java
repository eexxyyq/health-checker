package org.springboot.healthchecker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.metrics.MetricsEndpoint;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class StatusEndpointsService implements HealthIndicator {
    private MetricsEndpoint metricsEndpoint;
    @Value("${server.name}")
    private String serverName;

    @Override
    public Health health() {
        Map<String, Object> statusMap = new HashMap<>();
        MetricsEndpoint.MetricResponse metricResponse = metricsEndpoint.metric("process.uptime", null);

        long uptimeMilliseconds = (long) (metricResponse.getMeasurements().get(0).getValue() * 1000);
        long millis = uptimeMilliseconds % 1000;
        long second = (uptimeMilliseconds / 1000) % 60;
        long minute = (uptimeMilliseconds / (1000 * 60)) % 60;
        long hour = (uptimeMilliseconds / (1000 * 60 * 60)) % 24;
        long days = uptimeMilliseconds / (1000 * 60 * 60 * 24);
        String formattedUptime = String.format("%d.%02d:%02d:%02d.%03d", days, hour, minute, second, millis);

        statusMap.put("serverName", serverName);
        statusMap.put("uptime", formattedUptime);
        return Health.up().withDetails(statusMap).build();
    }

    @Autowired
    public void setMetricsEndpoint(MetricsEndpoint metricsEndpoint) {
        this.metricsEndpoint = metricsEndpoint;
    }
}
