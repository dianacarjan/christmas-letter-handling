package com.christmas.letter.sender.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record Letter(
        @Email(message = "Email should be valid")
        @NotNull(message = "Email should be provided")
        String email,
        @NotBlank(message = "Name should be provided")
        String name,
        @NotBlank(message = "Every child ought to have a Christmas wish list")
        String wishes,
        @NotNull(message = "Address is mandatory")
        @Valid
        Address address) {

}
