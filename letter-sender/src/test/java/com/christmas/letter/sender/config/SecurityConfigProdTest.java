package com.christmas.letter.sender.config;

import com.christmas.letter.sender.service.LetterSenderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
@ActiveProfiles("prod")
class SecurityConfigProdTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LetterSenderService letterSenderService;

    private static final String LETTER_API_PATH = "/api/v1/christmas-letters";

    @Test
    void givenSecuredEndpoint_whenGetMethod_thenReturnUnauthorized() throws Exception {
        mockMvc.perform(get(LETTER_API_PATH).secure(true))
                .andExpect(status().isUnauthorized());
    }
}
