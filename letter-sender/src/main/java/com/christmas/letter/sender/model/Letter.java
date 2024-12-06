package com.christmas.letter.sender.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record Letter(
        @Email(message = "Email should be valid")
        @NotNull(message = "Email should be provided")
        String email,
        @NotEmpty(message = "Name should be provided")
        String name,
        @NotEmpty(message = "Every child ought to have a Christmas wish list")
        String wishes,
        @NotEmpty(message = "Location is required")
        String location) {

}
