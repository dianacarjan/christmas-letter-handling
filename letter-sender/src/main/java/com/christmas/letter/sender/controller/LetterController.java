package com.christmas.letter.sender.controller;

import com.christmas.letter.sender.model.Letter;
import com.christmas.letter.sender.service.LetterSenderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/christmas-letters")
@RequiredArgsConstructor
public class LetterController {
    private final LetterSenderService letterSenderService;

    @PostMapping("")
    ResponseEntity<Object> sendLetter(@Valid @RequestBody Letter letter) {
        letterSenderService.sendLetter(letter);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
