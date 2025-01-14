package com.christmas.letter.processor.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressMessage {
        @NotBlank(message = "Street is required")
        String street;
        @NotBlank(message = "City is required")
        String city;
        @NotBlank(message = "State is required")
        @Size(min = 2, max = 2, message = "State must be a two-letter abbreviation")
        String state;
        @NotBlank(message = "Zipcode is required")
        @Pattern(regexp = "^\\d{5}(-\\d{4})?$", message = "Invalid zipcode")
        String zipCode;
}
