package com.christmas.letter.processor.listener;

import com.christmas.letter.processor.dto.LetterMessage;
import com.christmas.letter.processor.mapper.LetterMapper;
import com.christmas.letter.processor.model.Letter;
import com.christmas.letter.processor.repository.LetterRepository;
import io.awspring.cloud.sqs.annotation.SqsListener;
import io.awspring.cloud.sqs.annotation.SqsListenerAcknowledgementMode;
import io.awspring.cloud.sqs.listener.acknowledgement.Acknowledgement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Caching;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Validated
@Service
@RequiredArgsConstructor
public class LetterSqsListener {

	private final LetterRepository letterRepository;

	@Caching(
			evict = {@CacheEvict(value = "60m-letters", allEntries = true)},
			put = {@CachePut(value = "60m-letter", key = "#letterMessage.email")})
	@SqsListener(
			value = "${letter-processor.aws.sqs.queue-url}",
			acknowledgementMode = SqsListenerAcknowledgementMode.MANUAL)
	public Letter listen(
			@Valid @Payload LetterMessage letterMessage, Acknowledgement acknowledgement) {
		log.info("Letter Message received {}, on the listen method", letterMessage);
		Letter savedLetter =
				letterRepository.save(LetterMapper.INSTANCE.letterMessageToLetter(letterMessage));
		log.info("Letter saved successfully!");
		acknowledgement.acknowledge();

		return savedLetter;
	}
}
