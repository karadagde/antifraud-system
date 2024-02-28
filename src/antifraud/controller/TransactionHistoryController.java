package antifraud.controller;

import antifraud.model.dto.TransactionHistoryResponseDTO;
import antifraud.service.CreditCardService;
import antifraud.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/antifraud/history")
@Validated
public class TransactionHistoryController {
    private final TransactionService transactionService;
    private final CreditCardService creditCardService;

    public TransactionHistoryController(TransactionService transactionService, CreditCardService creditCardService) {
        this.transactionService = transactionService;
        this.creditCardService = creditCardService;
    }

    @GetMapping("")
    public ResponseEntity<List<TransactionHistoryResponseDTO>> getAllTransactions() {

        return ResponseEntity.ok(transactionService.getAllTransactions());
    }


    @GetMapping("/{number}")
    public ResponseEntity<List<TransactionHistoryResponseDTO>> getTransactionsByCardNumber(@PathVariable String number) {


        boolean isValidCard = creditCardService.validateCardNumber(number);

        if (!isValidCard) {
            return ResponseEntity.badRequest().build();
        }

        List<TransactionHistoryResponseDTO> transactions = transactionService.getTransactionsByCardNumber(number);


        if (transactions.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(transactions);
    }


}
