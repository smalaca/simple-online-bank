package com.smalaca.onlinebank.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CreditOfferController.class)
@org.springframework.context.annotation.Import(com.smalaca.onlinebank.config.DevSecurityConfig.class)
@org.springframework.test.context.ActiveProfiles("dev")
class CreditOfferControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnRandomizedCreditOffer() throws Exception {
        String json = """
                {
                    "firstName": "John",
                    "lastName": "Doe",
                    "amount": 5000,
                    "salary": 3000,
                    "currentCreditsAmount": 1000
                }
                """;

        mockMvc.perform(get("/api/credit-offers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(anyOf(
                        is("possible"),
                        is("possible but smaller"),
                        is("not possible")
                )));
    }
}
