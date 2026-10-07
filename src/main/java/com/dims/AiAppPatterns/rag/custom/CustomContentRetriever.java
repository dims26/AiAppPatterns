package com.dims.AiAppPatterns.rag.custom;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.query.Query;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.EmbeddingStore;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;

public class CustomContentRetriever implements ContentRetriever {

    public static final String DOMAIN_KEY = "domain";
    public static final int MAX_RESULTS = 10;
    private final EmbeddingModel embeddingModel;
    private final EmbeddingStore<TextSegment> store;
    private final String domain;

    public CustomContentRetriever(EmbeddingModel embeddingModel,
                                  @Qualifier("customItEmbedding") EmbeddingStore<TextSegment> store,
                                  String domain) {
        this.embeddingModel = embeddingModel;
        this.store = store;
        this.domain = domain;
    }

    @Override
    public List<Content> retrieve(Query query) {

        Embedding queryEmbedding = embeddingModel.embed(query.text()).content();

        EmbeddingSearchResult<TextSegment> embeddingSearchResult = store.search(EmbeddingSearchRequest.builder()
                .queryEmbedding(queryEmbedding)
                .maxResults(MAX_RESULTS)
                .build());
        List<EmbeddingMatch<TextSegment>> matches = embeddingSearchResult.matches();

        return matches.stream()
                .filter(m -> domain.equals(m.embedded().metadata().getString(DOMAIN_KEY)))
                .map(m -> Content.from(m.embedded().text()))
                .toList();
    }
}
