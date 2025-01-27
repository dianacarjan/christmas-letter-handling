package com.christmas.letter.processor.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class LetterMessage {
	@Email(message = "Email is not valid")
	private String email;

	@NotBlank(message = "Name is empty")
	private String name;

	@NotBlank(message = "Wishes is required")
	private String wishes;

	@Valid
	@NotNull(message = "Address is required") private AddressMessage address;
}
