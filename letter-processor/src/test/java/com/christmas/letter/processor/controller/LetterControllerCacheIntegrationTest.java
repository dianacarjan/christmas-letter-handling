package com.christmas.letter.processor.controller;

import com.christmas.letter.processor.helper.LetterUtils;
import com.christmas.letter.processor.helper.RedisTestContainer;
import com.christmas.letter.processor.listener.LetterSqsListener;
import com.christmas.letter.processor.model.Letter;
import com.christmas.letter.processor.repository.LetterRepository;
import com.christmas.letter.processor.service.LetterProcessorService;
import io.awspring.cloud.autoconfigure.dynamodb.DynamoDbAutoConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@EnableAutoConfiguration(exclude = {DynamoDbAutoConfiguration.class})
@ExtendWith(MockitoExtension.class)
@TestPropertySource("classpath:config-test.properties")
class LetterControllerCacheIntegrationTest extends RedisTestContainer {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LetterRepository letterRepository;

    @MockBean
    private LetterSqsListener letterSqsListener;

    @InjectMocks
    private LetterProcessorService letterProcessorService;

    @Autowired
    private RedisTemplate<String, ?> redisTemplate;

    private static final String LETTER_API_PATH = "/api/v1/christmas-letters";

    @BeforeEach
    void setup() {
        Set<String> keys = redisTemplate.keys("60m-letters:*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    @Test
    void givenPage_whenGetMethod_thenReturnCachedLetters() throws Exception {
        //Arrange
        Letter christmasLetter = LetterUtils.generateLetter();
        Pageable pageRequest =  PageRequest.of(0, 10, Sort.by("email"));
        List<Letter> content = new ArrayList<>();
        content.add(christmasLetter);
        Page<Letter> result = new PageImpl<>(content, pageRequest, 1);
        when(letterRepository.findAll(any(Pageable.class))).thenReturn(result);

        // Act & Assert
        assertThat(Objects.requireNonNull(redisTemplate.keys("60m-letters:*"))).isEmpty();

        mockMvc.perform(get(LETTER_API_PATH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(1));


        verify(letterRepository, times(1)).findAll(any(Pageable.class));

        mockMvc.perform(get(LETTER_API_PATH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(letterRepository, times(1)).findAll(any(Pageable.class));
        assertThat(Objects.requireNonNull(redisTemplate.keys("60m-letters:*")).size()).isOne();
    }
}
