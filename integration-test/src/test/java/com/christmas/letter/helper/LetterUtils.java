package com.christmas.letter.helper;

import net.bytebuddy.utility.RandomString;

public class LetterUtils {
    public static String generateRandomChristmasLetter(String email) {
        String name = RandomString.make();
        String wishes = RandomString.make();
        String location = RandomString.make();

        return String.format("""
                {
                	"email"   : "%s",
                	"name"  : "%s",
                	"wishes" : "%s",
                	"location" : "%s"
                }
                """, email, name, wishes, location);
    }
}
