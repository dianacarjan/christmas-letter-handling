package com.christmas.letter.processor.mapper;

import com.christmas.letter.processor.dto.LetterMessage;
import com.christmas.letter.processor.model.Letter;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface LetterMapper {
    LetterMapper INSTANCE = Mappers.getMapper(LetterMapper.class);

    LetterMessage letterToLetterMessage(Letter letter);
    Letter letterMessageToLetter(LetterMessage letterMessage);
}
