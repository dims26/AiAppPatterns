package com.dims.AiAppPatterns.rag.custom;

import com.dims.AiAppPatterns.AppProperties;
import com.opencsv.CSVReader;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class CustomEmbeddingIngestor {

    Logger logger = LoggerFactory.getLogger(CustomEmbeddingIngestor.class);

    private final EmbeddingModel embeddingModel;
    private final EmbeddingStore<TextSegment> store;
    private final AppProperties appProperties;

    public CustomEmbeddingIngestor(AppProperties appProperties, EmbeddingModel embeddingModel,
                                   EmbeddingStore<TextSegment> store) {
        this.appProperties = appProperties;
        this.embeddingModel = embeddingModel;
        this.store = store;
    }

    public void ingest() throws IOException {
        logger.info("Initializing CustomEmbeddingIngestor");

        Path topicsDir = Path.of(appProperties.paths().techDocsTopics());

        Map<String, List<SourceRow>> domainRowMap = new HashMap<>();

        try (Stream<Path> stream = Files.list(topicsDir)) {
            stream.filter(p -> p.toString().endsWith(".csv")).forEach(p -> {
                String name = p.getFileName().toString();
                String domain = name.substring(0, name.lastIndexOf(".csv"));

                List<SourceRow> rows = parseToSourceRow(p, domain);
                domainRowMap.put(domain, rows);
            });
        }

        domainRowMap.forEach((domain, rows) -> embedAndStore(rows));
        logger.info("CustomEmbeddingIngestor finished ingesting");
    }

    private List<SourceRow> parseToSourceRow(Path path, String domain) {

        try (CSVReader reader = new CSVReader(Files.newBufferedReader(path))) {

            return reader.readAll().stream()
                    .skip(1)
                    .map(r -> new SourceRow(domain, r[0], r[1]))
                    .toList();

        } catch (Exception e) {
            throw new RuntimeException("Failed parsing CSV: " + path, e);
        }
    }

    private void embedAndStore(List<SourceRow> rows) {
        for (SourceRow row : rows) {
            Metadata metadata = Metadata.from(Map.of("domain", row.domain(), "topic", row.topic()));
            TextSegment segment = TextSegment.from(row.topic() + "\n" + row.text(), metadata);

            Embedding embedding = embeddingModel.embed(segment).content();
            store.add(embedding, segment);
        }
    }
}
