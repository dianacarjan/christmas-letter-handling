package com.christmas.letter.processor.helper;

import com.christmas.letter.processor.dto.LetterMessage;
import com.christmas.letter.processor.mapper.LetterMapper;
import com.christmas.letter.processor.model.Address;
import com.christmas.letter.processor.model.Letter;
import lombok.experimental.UtilityClass;
import net.bytebuddy.utility.RandomString;

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

    public static Letter generateLetter() {
        return LetterUtils.generateLetter(LetterUtils.generateAddress());
    }


    public static LetterMessage generateLetterPayload() {

        return LetterMapper.INSTANCE.letterToLetterMessage(LetterUtils.generateLetter());
    }
}
