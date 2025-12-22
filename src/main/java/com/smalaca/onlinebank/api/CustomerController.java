package com.smalaca.onlinebank.api;

import com.smalaca.onlinebank.api.dto.CustomerDtos.CreateCustomerRequest;
import com.smalaca.onlinebank.api.dto.CustomerDtos.CustomerResponse;
import com.smalaca.onlinebank.application.CustomerService;
import com.smalaca.onlinebank.domain.Customer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    public ResponseEntity<CustomerResponse> create(@RequestBody CreateCustomerRequest req) {
        Customer saved = customerService.addCustomer(req.customerNumber(), req.name());
        CustomerResponse resp = new CustomerResponse(saved.getCustomerNumber(), saved.getName());
        return ResponseEntity.created(URI.create("/api/customers/" + saved.getCustomerNumber())).body(resp);
    }

    @GetMapping
    public List<CustomerResponse> all() {
        return customerService.findAll().stream()
                .map(c -> new CustomerResponse(c.getCustomerNumber(), c.getName()))
                .toList();
    }

    @GetMapping("/{number}")
    public CustomerResponse get(@PathVariable String number) {
        Customer c = customerService.getByNumber(number);
        return new CustomerResponse(c.getCustomerNumber(), c.getName());
    }
}
