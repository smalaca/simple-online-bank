package com.smalaca.onlinebank.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OfferPoliciesController.class)
@org.springframework.context.annotation.Import(com.smalaca.onlinebank.config.DevSecurityConfig.class)
@org.springframework.test.context.ActiveProfiles("dev")
class OfferPoliciesControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnPoliciesForExistingOffer() throws Exception {
        mockMvc.perform(get("/api/offers/EVERYDAY/policies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.offerId").value("EVERYDAY"))
                .andExpect(jsonPath("$.policies").isArray())
                .andExpect(jsonPath("$.policies[0].name").value("Minimum Age"));
    }

    @Test
    void shouldReturn404ForNonExistentOffer() throws Exception {
        mockMvc.perform(get("/api/offers/NON_EXISTENT/policies"))
                .andExpect(status().isNotFound());
    }
}
