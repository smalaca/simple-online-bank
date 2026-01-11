package com.smalaca.onlinebank.api;

import com.smalaca.onlinebank.api.dto.AccountDtos.OfferPolicyResponse;
import com.smalaca.onlinebank.api.dto.AccountDtos.PolicyResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/offers")
public class OfferPoliciesController {
    private static final Map<String, List<PolicyResponse>> POLICIES = Map.of(
            "EVERYDAY", List.of(
                    new PolicyResponse("Minimum Age", "Customer must be at least 18 years old."),
                    new PolicyResponse("Identity Verification", "Customer must provide valid identity document.")
            ),
            "SAVINGS", List.of(
                    new PolicyResponse("Initial Deposit", "Minimum initial deposit of 100 USD is required."),
                    new PolicyResponse("Minimum Age", "Customer must be at least 18 years old.")
            ),
            "PREMIUM", List.of(
                    new PolicyResponse("Minimum Balance", "Customer must maintain a minimum balance of 5000 USD."),
                    new PolicyResponse("Income Proof", "Proof of monthly income of at least 2000 USD.")
            )
    );

    @GetMapping("/{offerId}/policies")
    public ResponseEntity<OfferPolicyResponse> getPolicies(@PathVariable String offerId) {
        if (POLICIES.containsKey(offerId)) {
            return ResponseEntity.ok(new OfferPolicyResponse(offerId, POLICIES.get(offerId)));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
