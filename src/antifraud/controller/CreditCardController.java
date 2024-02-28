package antifraud.controller;

import antifraud.model.entity.CreditCard;
import antifraud.model.records.DeleteCardResponse;
import antifraud.model.records.StolenCardRegisterRequest;
import antifraud.service.CreditCardService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/antifraud/stolencard")
@Validated
public class CreditCardController {

    private final CreditCardService creditCardService;

    public CreditCardController(CreditCardService creditCardService) {
        this.creditCardService = creditCardService;
    }


    @GetMapping
    public ResponseEntity<List<CreditCard>> getAllStolenCards() {
        return ResponseEntity.ok(creditCardService.getAllCards());
    }

    @PostMapping
    public ResponseEntity<CreditCard> registerStolenCard(@Valid @RequestBody StolenCardRegisterRequest request) {
        boolean isValidCard = creditCardService.validateCardNumber(request.number());
        if (!isValidCard) {
            return ResponseEntity.badRequest().build();
        }

        boolean isCardAlreadyBlacklisted = creditCardService.cardIsBlacklisted(request.number());

        if (isCardAlreadyBlacklisted) {
            return ResponseEntity.status(409).build();
        }

        return ResponseEntity.ok(creditCardService.registerCard(request.number()));


    }

    @DeleteMapping("/{number}")
    public ResponseEntity<DeleteCardResponse> deleteCard(@PathVariable String number) {
        boolean isValid = creditCardService.validateCardNumber(number);
        if (!isValid) {
            return ResponseEntity.badRequest().build();
        }
        boolean cardExist = creditCardService.cardIsBlacklisted(number);
        if (!cardExist) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(creditCardService.deleteCard(number));


    }


}
