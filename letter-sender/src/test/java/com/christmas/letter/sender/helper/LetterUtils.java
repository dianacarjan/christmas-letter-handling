package com.christmas.letter.sender.helper;

import com.christmas.letter.sender.model.Address;
import com.christmas.letter.sender.model.Letter;
import java.util.stream.Stream;
import lombok.experimental.UtilityClass;
import net.bytebuddy.utility.RandomString;
import org.junit.jupiter.params.provider.Arguments;

@UtilityClass
public class LetterUtils {
	public static Address generateAddress() {
		return new Address("SpressStrasse 2", "Berlin", "DE", "34523");
	}

	public static Letter generateLetter(Address address) {
		String email = String.format("%s@example.com", RandomString.make());
		String name = RandomString.make();
		String wishes = RandomString.make();

		return new Letter(email, name, wishes, address);
	}

	public static Stream<Arguments> provideInvalidLetters() {
		Address senderLocation = LetterUtils.generateAddress();

		return Stream.of(
				Arguments.of(
						new Letter("23", "Jane", "Some books", senderLocation),
						"Email should be valid"),
				Arguments.of(
						new Letter("siobhan@example.com", "Siobhan", null, senderLocation),
						"Every child ought to have a Christmas wish list"),
				Arguments.of(
						new Letter("siobhan@example.com", "Siobhan", "More books", null),
						"Address is mandatory"),
				Arguments.of(
						LetterUtils.generateLetter(
								new Address(
										null,
										senderLocation.city(),
										senderLocation.state(),
										senderLocation.zipCode())),
						"Street is mandatory"),
				Arguments.of(
						LetterUtils.generateLetter(
								new Address(
										senderLocation.street(),
										"",
										senderLocation.state(),
										senderLocation.zipCode())),
						"City is mandatory"),
				Arguments.of(
						LetterUtils.generateLetter(
								new Address(
										senderLocation.street(),
										senderLocation.city(),
										"Germany",
										senderLocation.zipCode())),
						"State must be a two-letter abbreviation"),
				Arguments.of(
						LetterUtils.generateLetter(
								new Address(
										senderLocation.street(),
										senderLocation.city(),
										senderLocation.state(),
										"123")),
						"Invalid zipcode"));
	}
}
