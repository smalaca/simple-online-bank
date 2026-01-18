package com.smalaca.onlinebank.api;

import com.smalaca.onlinebank.api.dto.AccountDtos.CreditOfferRequest;
import com.smalaca.onlinebank.api.dto.AccountDtos.CreditOfferResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Random;

@RestController
@RequestMapping("/api/credit-offers")
public class CreditOfferController {
    private static final String CONTACT_DETAILS = "Contact us at +1 800 ONLINE BANK or visit any branch.";
    private final Random random = new Random();

    @PostMapping
    @Operation(
            description = "Credit offer for customers",
            summary = "Generate credit offer based on provided request")
    @ApiResponse(responseCode = "201", description = "Information about possibility of credit.")
    public CreditOfferResponse generate(@RequestBody CreditOfferRequest request) {
        int decision = random.nextInt(100);

        if (!request.hasCustomerName()) {
            return new CreditOfferResponse("error - customer name and lastname are required", null, null, CONTACT_DETAILS);
        }

        if (decision < 20) {
            return new CreditOfferResponse("not possible", null, "Credit score is too low.", null);
        } else if (decision < 50) {
            BigDecimal maxPossible = request.salary().multiply(BigDecimal.valueOf(random.nextInt(5) + 2));
            return new CreditOfferResponse("possible but smaller", maxPossible, null, null);
        } else {
            return new CreditOfferResponse("possible", null, null, CONTACT_DETAILS);
        }
    }
}
