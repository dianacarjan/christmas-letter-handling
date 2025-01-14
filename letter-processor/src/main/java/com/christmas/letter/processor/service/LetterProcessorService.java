package com.christmas.letter.processor.service;

import com.christmas.letter.processor.dto.CachedPage;
import com.christmas.letter.processor.exception.NotFoundException;
import com.christmas.letter.processor.model.Letter;
import com.christmas.letter.processor.repository.LetterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LetterProcessorService {

    private final LetterRepository letterRepository;

    @Cacheable(value = "60m-letters", keyGenerator = "pageableKeyGenerator", cacheResolver = "configurableRedisCacheResolver")
    public CachedPage<Letter> getAll(Pageable pageable) {
        return new CachedPage<>(letterRepository.findAll(pageable), pageable);
    }

    @Cacheable(value = "60m-letter", key = "#email", cacheResolver = "configurableRedisCacheResolver")
    public Letter getLetterByEmail(String email) {
        return letterRepository.findById(email)
                .orElseThrow(() -> new NotFoundException(String.format("Letter of email %s not found!", email)));
    }
}
