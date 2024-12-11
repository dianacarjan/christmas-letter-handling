package com.christmas.letter.sender.service;

import com.christmas.letter.sender.config.SnsConfigProperties;
import com.christmas.letter.sender.model.Letter;
import io.awspring.cloud.sns.core.SnsTemplate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LetterSenderServiceTest {
    @Mock
    private SnsTemplate snsTemplate;

    @Mock
    private SnsConfigProperties snsConfigProperties;

    @InjectMocks
    private LetterSenderService letterSenderService;

    @Test
    void givenLetter_whenSendLetter_letterPublishedToSns(){
        // Arrange
        Letter letter = new Letter("siobhan@example.com", "Siobhan", "books", "Frankfurt");
        String topicArn = "arn:aws:sns:us-east-1:000000000000:letter-created";

        when(snsConfigProperties.getTopicArn()).thenReturn(topicArn);

        // Act
        letterSenderService.sendLetter(letter);

        // Assert
        verify(snsTemplate, times(1)).convertAndSend(topicArn, letter);
    }
}
