package com.dims.AiAppPatterns.rag.custom;

import com.dims.AiAppPatterns.AppProperties;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.store.embedding.EmbeddingStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Component
public class CustomRetrieverRegistry {
    private final AppProperties appProperties;
    private final EmbeddingModel embeddingModel;
    private final EmbeddingStore<TextSegment> store;

    private Map<ContentRetriever, String> retrieverDomainMap = null;

    public CustomRetrieverRegistry(AppProperties appProperties, EmbeddingModel embeddingModel,
                                   @Qualifier("customItEmbedding") EmbeddingStore<TextSegment> store) {
        this.appProperties = appProperties;
        this.embeddingModel = embeddingModel;
        this.store = store;
    }

    public Map<ContentRetriever, String> getRetrieverDomainMap() throws IOException {
        if (retrieverDomainMap == null) {
            retrieverDomainMap = new HashMap<>();

            for (String domain : getDomains()) {
                CustomContentRetriever retriever = new CustomContentRetriever(embeddingModel, store, domain);
                retrieverDomainMap.put(retriever, domain);
            }
        }
        return Map.copyOf(retrieverDomainMap);
    }

    private List<String> getDomains() throws IOException {
        List<String> domains = new ArrayList<>();

        Path topicsDir = Path.of(appProperties.paths().techDocsTopics());

        try (Stream<Path> stream = Files.list(topicsDir)) {
            stream.filter(p -> p.toString().endsWith(".csv")).forEach(p -> {
                String name = p.getFileName().toString();
                String domain = name.substring(0, name.lastIndexOf(".csv"));

                domains.add(domain);
            });
        }

        return domains;
    }
}
