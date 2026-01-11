package com.smalaca.onlinebank.api;

import com.smalaca.onlinebank.api.dto.AccountDtos.AccountDeletionResponse;
import com.smalaca.onlinebank.application.AccountService;
import com.smalaca.onlinebank.application.AccountService.AccountDeletionResult;
import com.smalaca.onlinebank.domain.Account;
import com.smalaca.onlinebank.domain.Transaction;
import com.smalaca.onlinebank.domain.TransactionType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
    void shouldReturn200AndValidationErrorsWhenDepositAmountIsNonPositive() throws Exception {
        mockMvc.perform(put("/api/accounts/123/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\": -10}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].field").value("amount"))
                .andExpect(jsonPath("$[0].message").exists());

        mockMvc.perform(put("/api/accounts/123/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\": 0}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].field").value("amount"))
                .andExpect(jsonPath("$[0].message").exists());
    }

    @Test
    void shouldReturn200AndValidationErrorsWhenWithdrawAmountIsNonPositive() throws Exception {
        mockMvc.perform(put("/api/accounts/123/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\": -5}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].field").value("amount"))
                .andExpect(jsonPath("$[0].message").exists());
    }

    @Test
    void shouldReturn200AndValidationErrorsWhenTransferAmountIsNonPositive() throws Exception {
        mockMvc.perform(put("/api/accounts/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"sourceAccount\": \"123\", \"targetAccount\": \"456\", \"amount\": 0}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].field").value("amount"))
                .andExpect(jsonPath("$[0].message").exists());
    }

    @Test
    void shouldReturnTransactionHistory() throws Exception {
        Account source = mock(Account.class);
        given(source.getAccountNumber()).willReturn("123");
        Account target = mock(Account.class);
        given(target.getAccountNumber()).willReturn("456");
        Transaction transaction = new Transaction(source, target, TransactionType.TRANSFER, new BigDecimal("100.00"));
        given(accountService.getHistory("123")).willReturn(List.of(transaction));

        mockMvc.perform(get("/api/accounts/123/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].type").value("TRANSFER"))
                .andExpect(jsonPath("$[0].amount").value(100.00))
                .andExpect(jsonPath("$[0].from").value("123"))
                .andExpect(jsonPath("$[0].to").value("456"));
    }

    @Test
    void shouldReturn404WhenGettingHistoryForNonExistentAccount() throws Exception {
        given(accountService.getHistory("non-existent")).willThrow(new NoSuchElementException("Account not found"));

        mockMvc.perform(get("/api/accounts/non-existent/history"))
                .andExpect(status().isNotFound());
    }
}
