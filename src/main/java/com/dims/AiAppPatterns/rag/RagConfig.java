package com.dims.AiAppPatterns.rag;

import com.dims.AiAppPatterns.AppProperties;
import com.dims.AiAppPatterns.SystemPrompts;
import com.dims.AiAppPatterns.rag.custom.CustomEmbeddingIngestor;
import com.dims.AiAppPatterns.rag.custom.CustomQueryRouter;
import com.dims.AiAppPatterns.rag.custom.CustomQueryTransformer;
import com.dims.AiAppPatterns.rag.custom.CustomRetrieverRegistry;
import com.dims.AiAppPatterns.rag.sources.ItSourcePreprocessor;
import com.dims.AiAppPatterns.rag.sources.TopicCategorizer;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.aggregator.DefaultContentAggregator;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

@Configuration
public class RagConfig {
    Logger logger = LoggerFactory.getLogger(RagConfig.class);

    private final AppProperties appProperties;

    public RagConfig(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    @Order(HIGHEST_PRECEDENCE)
    @Bean
    public ItSourcePreprocessor itSourcePreprocessor(TopicCategorizer topicCategorizer) {
        return new ItSourcePreprocessor(topicCategorizer, appProperties);
    }

    @Primary
    @Bean(name = "defaultItEmbedding")
    public EmbeddingStore<TextSegment> defaultItSourceEmbeddingsStore() {
        logger.info("Initializing embedding store");

        List<Document> documents = FileSystemDocumentLoader.loadDocuments(appProperties.paths().techDocsTopics());
        InMemoryEmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
        EmbeddingStoreIngestor.ingest(documents, embeddingStore);
        logger.info("Embeddings Ingested");

        String storeName = "default-embeddings.json";
        try (FileOutputStream ignored = new FileOutputStream(
                appProperties.paths().topicEmbeddingsStore() + storeName)) {
            embeddingStore.serializeToFile(Path.of(appProperties.paths().topicEmbeddingsStore(), storeName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return embeddingStore;
    }

    @Bean(name = "defaultKnowledgeWizard")
    public ItKnowledgeWizard itKnowledgeWizard(
            ChatModel chatModel,
            @Qualifier("defaultItEmbedding") EmbeddingStore<TextSegment> embeddingStore) {
        return AiServices.builder(ItKnowledgeWizard.class)
                .chatModel(chatModel)
                .systemMessage(SystemPrompts.IT_KNOWLEDGE_WIZARD)
                .contentRetriever(EmbeddingStoreContentRetriever.from(embeddingStore))
                .build();
    }

    @Bean(name = "customItEmbedding")
    public InMemoryEmbeddingStore<TextSegment> customEmbeddingStore() {
        return new InMemoryEmbeddingStore<>();
    }

    @Bean
    public CustomEmbeddingIngestor customEmbeddingIngestor(
            EmbeddingModel embeddingModel,
            @Qualifier("customItEmbedding") InMemoryEmbeddingStore<TextSegment> store) throws IOException {
        CustomEmbeddingIngestor ingestor = new CustomEmbeddingIngestor(appProperties, embeddingModel, store);
        ingestor.ingest();

        String storeName = "custom-embeddings.json";
        try (FileOutputStream ignored = new FileOutputStream(
                appProperties.paths().topicEmbeddingsStore() + storeName)) {
            store.serializeToFile(Path.of(appProperties.paths().topicEmbeddingsStore(), storeName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return ingestor;
    }

    @Bean
    public CustomQueryRouter customQueryRouter(
            ChatModel chatModel, CustomRetrieverRegistry retrieverRegistry) throws IOException {
        return new CustomQueryRouter(chatModel, retrieverRegistry.getRetrieverDomainMap());
    }

    @Bean("customRetrievalAugmentor")
    public RetrievalAugmentor retrievalAugmentor(
            CustomQueryTransformer queryTransformer, CustomQueryRouter queryRouter) {
        return DefaultRetrievalAugmentor.builder()
                .queryTransformer(queryTransformer)
                .queryRouter(queryRouter)
                .contentAggregator(new DefaultContentAggregator())
                .build();
    }

    @Bean(name = "customAugmentedKnowledgeWizard")
    public ItKnowledgeWizard customAugItKnowledgeWizard(
            ChatModel chatModel,
            @Qualifier("customRetrievalAugmentor") RetrievalAugmentor retrievalAugmentor) {
        return AiServices.builder(ItKnowledgeWizard.class)
                .chatModel(chatModel)
                .systemMessage(SystemPrompts.IT_KNOWLEDGE_WIZARD)
                .retrievalAugmentor(retrievalAugmentor)
                .build();
    }

    @Bean(name = "customKnowledgeWizard")
    public ItKnowledgeWizard customItKnowledgeWizard(
            ChatModel chatModel, EmbeddingModel embeddingModel,
            @Qualifier("customItEmbedding") EmbeddingStore<TextSegment> embeddingStore) {
        return AiServices.builder(ItKnowledgeWizard.class)
                .chatModel(chatModel)
                .systemMessage(SystemPrompts.IT_KNOWLEDGE_WIZARD)
                .contentRetriever(EmbeddingStoreContentRetriever
                        .builder()
                        .embeddingStore(embeddingStore)
                        .embeddingModel(embeddingModel)
                        .build()
                ).build();
    }
}
