package com.christmas.letter.processor.exception;

import java.util.List;
import java.util.Map;

public record ErrorResponse(String message, List<Map<String, String>> errors) {}
