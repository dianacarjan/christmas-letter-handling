package com.christmas.letter.sender.exception;

import java.util.List;

public record ErrorResponse(String message, List<String> details) {}
