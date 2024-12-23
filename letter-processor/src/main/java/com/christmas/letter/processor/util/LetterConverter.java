package com.christmas.letter.processor.util;

import com.christmas.letter.processor.dto.AddressMessage;
import com.christmas.letter.processor.dto.LetterMessage;
import com.christmas.letter.processor.model.Address;
import com.christmas.letter.processor.model.Letter;
import lombok.experimental.UtilityClass;

@UtilityClass
public class LetterConverter {
    public static Letter toModel(LetterMessage letterMessage) {
        return Letter.builder()
                .name(letterMessage.getName())
                .email(letterMessage.getEmail())
                .wishes(letterMessage.getWishes())
                .address(LetterConverter.toModel(letterMessage.getAddress()))
                .build();
    }

    public static Address toModel(AddressMessage addressMessage) {
        return Address.builder()
                .street(addressMessage.getStreet())
                .state(addressMessage.getState())
                .city(addressMessage.getCity())
                .zipCode(addressMessage.getZipCode())
                .build();
    }
}
