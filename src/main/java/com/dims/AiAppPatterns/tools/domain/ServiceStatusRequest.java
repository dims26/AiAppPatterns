package com.dims.AiAppPatterns.tools.domain;

import dev.langchain4j.agent.tool.P;

public record ServiceStatusRequest(@P("Service name") String serviceName) {}
