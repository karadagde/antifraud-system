package antifraud.controller;

import antifraud.model.dto.TransactionDTO;
import antifraud.model.dto.TransactionHistoryResponseDTO;
import antifraud.model.dto.TransactionResponseDTO;
import antifraud.model.records.TransactionFeedbackRequest;
import antifraud.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/antifraud/transaction")
@Validated
public class TransactionController {


    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {


        this.transactionService = transactionService;
    }


    @PostMapping
    public ResponseEntity<TransactionResponseDTO> assessTransaction(@Valid @RequestBody TransactionDTO transaction) {

        Optional<TransactionResponseDTO> assessedTransaction = transactionService.assesTransaction(transaction);


        return assessedTransaction.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.badRequest().build());


    }


    @PutMapping
    public ResponseEntity<TransactionHistoryResponseDTO> addFeedback(@Valid @RequestBody TransactionFeedbackRequest request) {
     

        return transactionService.updateTransaction(request);
    }
}
