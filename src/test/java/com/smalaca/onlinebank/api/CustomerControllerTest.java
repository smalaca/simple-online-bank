package com.smalaca.onlinebank.api;

import com.smalaca.onlinebank.api.dto.CustomerDtos.CustomerDeletionResponse;
import com.smalaca.onlinebank.application.AccountService;
import com.smalaca.onlinebank.application.CustomerService;
import com.smalaca.onlinebank.application.CustomerService.CustomerDeletionResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CustomerController.class)
@org.springframework.context.annotation.Import(com.smalaca.onlinebank.config.DevSecurityConfig.class)
@org.springframework.test.context.ActiveProfiles("dev")
class CustomerControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerService customerService;

    @MockBean
    private AccountService accountService;

    @Test
    void shouldReturn404WhenCustomerDoesNotExist() throws Exception {
        given(customerService.deleteCustomer(anyString())).willThrow(new NoSuchElementException("Customer not found"));

        mockMvc.perform(delete("/api/customers/CUST-1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn200AndSuccessMessageWhenCustomerSuccessfullyRemoved() throws Exception {
        given(customerService.deleteCustomer("CUST-1")).willReturn(CustomerDeletionResult.success("CUST-1"));

        mockMvc.perform(delete("/api/customers/CUST-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Customer CUST-1 removed successfully"));
    }

    @Test
    void shouldReturn200AndErrorMessageWhenCustomerHasAccounts() throws Exception {
        given(customerService.deleteCustomer("CUST-1")).willReturn(CustomerDeletionResult.error("CUST-1"));

        mockMvc.perform(delete("/api/customers/CUST-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Customer CUST-1 has accounts and cannot be removed"));
    }
}
