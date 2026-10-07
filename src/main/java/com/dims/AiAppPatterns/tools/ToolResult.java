package com.dims.AiAppPatterns.tools;

import java.util.List;
import java.util.Map;

public record ToolResult(String content, List<Map.Entry<String, String>> executions) {}
