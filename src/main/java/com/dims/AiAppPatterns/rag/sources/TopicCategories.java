package com.dims.AiAppPatterns.rag.sources;

import java.util.List;
import java.util.Map;

public record TopicCategories(Map<String, List<String>> categoryTopicsMap) {}
