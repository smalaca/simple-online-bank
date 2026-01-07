package com.smalaca.onlinebank.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class CustomerTest {
    @Test
    void shouldUpdateOnlyProvidedFields() {
        Customer customer = new Customer("AB-12-1234", "John", "Doe", "john@doe.com", "+48123456789", "Old Address");

        customer.update("Jane", null, "  ", "+48987654321", null);

        assertThat(customer.getName()).isEqualTo("Jane");
        assertThat(customer.getSurname()).isEqualTo("Doe");
        assertThat(customer.getEmail()).isEqualTo("john@doe.com");
        assertThat(customer.getPhoneNumber()).isEqualTo("+48987654321");
        assertThat(customer.getAddress()).isEqualTo("Old Address");
    }
}
