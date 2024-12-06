package com.christmas.letter.sender.service;

import com.christmas.letter.sender.config.SnsConfigProperties;
import com.christmas.letter.sender.model.Letter;
import io.awspring.cloud.sns.core.SnsTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LetterSenderService {

    private final SnsTemplate snsTemplate;
    private final SnsConfigProperties snsConfigProperties;


    public void sendLetter(Letter letter) {
        final String topicArn = snsConfigProperties.getTopicArn();
        snsTemplate.convertAndSend(topicArn, letter);
        log.info("Successfully published message to topic ARN: {}", topicArn);
    }
}
