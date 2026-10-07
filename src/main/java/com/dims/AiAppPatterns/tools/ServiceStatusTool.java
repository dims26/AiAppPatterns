package com.dims.AiAppPatterns.tools;

import com.dims.AiAppPatterns.tools.domain.ServiceStatusRequest;
import com.dims.AiAppPatterns.tools.domain.ServiceStatusResult;
import com.dims.AiAppPatterns.tools.stores.ServiceStatusStore;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Component;

@Component
public class ServiceStatusTool {

    private final ServiceStatusStore store;

    public ServiceStatusTool(ServiceStatusStore store) {
        this.store = store;
    }

    @Tool("""
            Get the current operational status of an IT service.
            
            Use this tool when the user asks about service status:
            Examples:
            - is a service available
            - is a service down
            - outage information
            - degraded performance
            """)
    public ServiceStatusResult getStatus(ServiceStatusRequest request) {
        return store.find(request.serviceName());
    }
}
