package com.smalaca.onlinebank.api;

import com.smalaca.onlinebank.api.dto.AccountDtos.AccountDeletionResponse;
import com.smalaca.onlinebank.application.AccountService;
import com.smalaca.onlinebank.application.AccountService.AccountDeletionResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)
@org.springframework.context.annotation.Import(com.smalaca.onlinebank.config.DevSecurityConfig.class)
@org.springframework.test.context.ActiveProfiles("dev")
class AccountControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @Test
    void shouldReturn404WhenAccountDoesNotExist() throws Exception {
        given(accountService.deleteAccount(anyString())).willThrow(new NoSuchElementException("Account not found"));

        mockMvc.perform(delete("/api/accounts/123"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn200AndSuccessMessageWhenAccountSuccessfullyRemoved() throws Exception {
        given(accountService.deleteAccount("123")).willReturn(AccountDeletionResult.success("123"));

        mockMvc.perform(delete("/api/accounts/123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Account 123 removed successfully"));
    }

    @Test
    void shouldReturn200AndErrorMessageWhenAccountHasBalance() throws Exception {
        given(accountService.deleteAccount("123")).willReturn(AccountDeletionResult.error("123", new BigDecimal("100.00")));

        mockMvc.perform(delete("/api/accounts/123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Account 123 has balance 100.00 and cannot be removed"));
    }

    @Test
    void shouldReturn400WhenDepositAmountIsNonPositive() throws Exception {
        mockMvc.perform(put("/api/accounts/123/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\": -10}"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(put("/api/accounts/123/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\": 0}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenWithdrawAmountIsNonPositive() throws Exception {
        mockMvc.perform(put("/api/accounts/123/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\": -5}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenTransferAmountIsNonPositive() throws Exception {
        mockMvc.perform(put("/api/accounts/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"sourceAccount\": \"123\", \"targetAccount\": \"456\", \"amount\": 0}"))
                .andExpect(status().isBadRequest());
    }
}
