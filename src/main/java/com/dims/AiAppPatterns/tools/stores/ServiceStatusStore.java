package com.dims.AiAppPatterns.tools.stores;

import com.dims.AiAppPatterns.tools.domain.ServiceStatus;
import com.dims.AiAppPatterns.tools.domain.ServiceStatusResult;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class ServiceStatusStore {
    private static final Random RANDOM = new Random();

    public ServiceStatusResult find(String serviceName) {
        return switch (RANDOM.nextInt(4)) {
            case 0 -> new ServiceStatusResult(serviceName, ServiceStatus.AVAILABLE, "Operating normally");
            case 1 -> new ServiceStatusResult(serviceName, ServiceStatus.DEGRADED, "Emergency maintenance");
            case 2 -> new ServiceStatusResult(serviceName, ServiceStatus.UNAVAILABLE, "Service interrupted");
            case 3 -> new ServiceStatusResult(serviceName, ServiceStatus.UNKNOWN, "Unknown service");
            default -> throw new IllegalStateException();
        };
    }
}
