package com.christmas.letter.processor.controller;

import com.christmas.letter.processor.model.Letter;
import com.christmas.letter.processor.service.LetterProcessorService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/christmas-letters")
@RequiredArgsConstructor
public class LetterController {

    private final LetterProcessorService letterProcessorService;

    @GetMapping
    Page<Letter> getAll(@PageableDefault Pageable pageable) {
        return letterProcessorService.getAll(pageable);
    }

    @GetMapping("/{email}")
    Letter getByEmail(@Valid @Email(message = "Invalid email") @PathVariable String email) {
        return letterProcessorService.getLetterByEmail(email);
    }
}
