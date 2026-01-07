package com.smalaca.onlinebank.api;

import com.smalaca.onlinebank.api.dto.CustomerDtos.CustomerDeletionResponse;
import com.smalaca.onlinebank.api.dto.CustomerDtos.CustomerResponse;
import com.smalaca.onlinebank.application.AccountService;
import com.smalaca.onlinebank.application.CustomerService;
import com.smalaca.onlinebank.application.CustomerService.CustomerDeletionResult;
import com.smalaca.onlinebank.domain.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.NoSuchElementException;

import static org.hamcrest.Matchers.hasItems;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
    void shouldCreateCustomer() throws Exception {
        Customer customer = new Customer("AB-12-1234-1234-1234-1234", "John", "Doe", "john.doe@test.com", "+48123456789", "Address");
        given(customerService.addCustomer(any(), any(), any(), any(), any())).willReturn(customer);

        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"John\", \"surname\": \"Doe\", \"email\": \"john.doe@test.com\", \"phoneNumber\": \"+48123456789\", \"address\": \"Address\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.customerNumber").value("AB-12-1234-1234-1234-1234"))
                .andExpect(jsonPath("$.name").value("John"))
                .andExpect(jsonPath("$.surname").value("Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@test.com"))
                .andExpect(jsonPath("$.phoneNumber").value("+48123456789"))
                .andExpect(jsonPath("$.address").value("Address"));
    }

    @Test
    void shouldReturn200AndValidationErrorsWhenNameOrSurnameMissing() throws Exception {
        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"john.doe@test.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].field").value(hasItems("name", "surname")));
    }

    @Test
    void shouldReturn200AndValidationErrorsWhenEmailIsInvalid() throws Exception {
        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"John\", \"surname\": \"Doe\", \"email\": \"invalid-email\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].field").value("email"))
                .andExpect(jsonPath("$[0].message").exists());
    }

    @Test
    void shouldReturnAllCustomersWithFullData() throws Exception {
        Customer customer = new Customer("AB-12-1234-1234-1234-1234", "John", "Doe", "john.doe@test.com", "+48123456789", "Address");
        given(customerService.findAll()).willReturn(List.of(customer));
        given(accountService.listCustomerAccounts(anyString())).willReturn(List.of());

        mockMvc.perform(get("/api/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].customerNumber").value("AB-12-1234-1234-1234-1234"))
                .andExpect(jsonPath("$[0].name").value("John"))
                .andExpect(jsonPath("$[0].surname").value("Doe"))
                .andExpect(jsonPath("$[0].email").value("john.doe@test.com"))
                .andExpect(jsonPath("$[0].phoneNumber").value("+48123456789"))
                .andExpect(jsonPath("$[0].address").value("Address"));
    }

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

    @Test
    void shouldUpdateCustomer() throws Exception {
        Customer customer = new Customer("AB-12-1234-1234-1234-1234", "Jane", "Doe", "jane.doe@test.com", "+48987654321", "New Address");
        given(customerService.updateCustomer(anyString(), any(), any(), any(), any(), any())).willReturn(customer);
        given(accountService.listCustomerAccounts(anyString())).willReturn(List.of());

        mockMvc.perform(put("/api/customers/AB-12-1234-1234-1234-1234")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Jane\", \"surname\": \"Doe\", \"email\": \"jane.doe@test.com\", \"phoneNumber\": \"+48987654321\", \"address\": \"New Address\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerNumber").value("AB-12-1234-1234-1234-1234"))
                .andExpect(jsonPath("$.name").value("Jane"))
                .andExpect(jsonPath("$.surname").value("Doe"))
                .andExpect(jsonPath("$.email").value("jane.doe@test.com"))
                .andExpect(jsonPath("$.phoneNumber").value("+48987654321"))
                .andExpect(jsonPath("$.address").value("New Address"));
    }

    @Test
    void shouldReturn404WhenUpdatingNonExistingCustomer() throws Exception {
        given(customerService.updateCustomer(anyString(), any(), any(), any(), any(), any())).willThrow(new NoSuchElementException("Customer not found"));

        mockMvc.perform(put("/api/customers/NON-EXISTING")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Jane\", \"surname\": \"Doe\", \"email\": \"jane.doe@test.com\", \"phoneNumber\": \"+48987654321\", \"address\": \"New Address\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn200AndValidationErrorsWhenUpdatingWithInvalidEmail() throws Exception {
        mockMvc.perform(put("/api/customers/AB-12-1234-1234-1234-1234")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Jane\", \"surname\": \"Doe\", \"email\": \"invalid-email\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].field").value("email"));
    }
}
