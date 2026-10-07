package com.dims.AiAppPatterns.rag.sources;

import com.dims.AiAppPatterns.AppProperties;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

public class ItSourcePreprocessor {

    Logger logger = LoggerFactory.getLogger(ItSourcePreprocessor.class);

    private final AppProperties appProperties;
    private final TopicCategorizer topicCategorizer;

    public ItSourcePreprocessor(TopicCategorizer topicCategorizer, AppProperties appProperties) {
        this.topicCategorizer = topicCategorizer;
        this.appProperties = appProperties;
        preprocess();
    }

    public void preprocess() {
        logger.info("Initializing preprocessor");
        try (Stream<Path> pathStream = Files.list(Path.of(appProperties.paths().techDocsTopics()))) {
            boolean topicsDirEmpty = pathStream.findFirst().isEmpty();
            if (topicsDirEmpty) {
                TopicCategories topicCategories = categorizeItSourceTopics();
                logger.info("IT sources preprocessed");
                logger.debug("Topic Categories: {}", topicCategories);
            }
        } catch (IOException | CsvValidationException e) {
            throw new RuntimeException(e);
        }
        logger.info("Preprocessor initialized");
    }

    private TopicCategories categorizeItSourceTopics() throws IOException, CsvValidationException {
        try (CSVReader csvReader = new CSVReaderBuilder(new FileReader(
                "datasets/technical-documents/synthetic_knowledge_items.csv")).withSkipLines(1).build()) {

            String[] row;
            List<String> topics = new ArrayList<>();
            while ((row = csvReader.readNext()) != null) {
                String topicValue = row[0];
                topics.add(topicValue);
            }

            TopicCategories topicCategories = topicCategorizer.categorize(topics);
            createItTopicCsvs(topicCategories);

            return topicCategories;
        }
    }

    private void createItTopicCsvs(TopicCategories topicCategories) throws IOException, CsvValidationException {
        Map<String, List<String>> topicMap = topicCategories.categoryTopicsMap();

        String topicDirPath = "datasets/technical-documents/topics/";
        emptyDirectory(topicDirPath);

        Map<String, CSVWriter> writers = new HashMap<>();
        System.out.println(topicMap.keySet());
        try {
            for (String category : topicMap.keySet()) {
                writers.put(category, new CSVWriter(new FileWriter("%s%s.csv".formatted(topicDirPath, category))));
            }

            try (CSVReader reader = new CSVReader(
                    new FileReader("datasets/technical-documents/synthetic_knowledge_items.csv"))) {

                String[] line;

                String[] header = reader.readNext();
                if (header != null) {
                    for (CSVWriter writer : writers.values()) {
                        writer.writeNext(Arrays.copyOfRange(header, 0, 2));
                    }
                }

                while ((line = reader.readNext()) != null) {
                    String topic = line[0];

                    for (Map.Entry<String, List<String>> entry : topicMap.entrySet()) {
                        String category = entry.getKey();
                        List<String> topics = entry.getValue();

                        if (topics.contains(topic)) {
                            writers.get(category).writeNext(Arrays.copyOfRange(line, 0, 2));
                            break;
                        }
                    }
                }
            }
        } finally {
            for (CSVWriter writer : writers.values()) {
                writer.close();
            }
        }
    }

    private void emptyDirectory(String dir) throws IOException {
        Path dirPath = Path.of(dir);
        if (!Files.exists(dirPath)) return;

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dirPath)) {
            for (Path entry : stream) {
                Files.deleteIfExists(entry);
            }
        }
    }
}
