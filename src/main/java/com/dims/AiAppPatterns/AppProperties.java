package com.dims.AiAppPatterns;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public record AppProperties(Paths paths) {
    public record Paths(String techDocsTopics, String topicEmbeddingsStore) {}
}
