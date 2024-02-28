package antifraud.service;

import antifraud.model.dto.TransactionDTO;
import antifraud.model.dto.TransactionHistoryResponseDTO;
import antifraud.model.dto.TransactionResponseDTO;
import antifraud.model.entity.TransactionHistory;
import antifraud.model.entity.TransactionLimit;
import antifraud.model.enums.Region;
import antifraud.model.enums.TransactionFeedback;
import antifraud.model.enums.TransactionResult;
import antifraud.model.inteface.TransactionLimitAction;
import antifraud.model.records.TransactionFeedbackRequest;
import antifraud.repository.TransactionLimitRepository;
import antifraud.repository.TransactionRepository;
import antifraud.utils.AdjustTransactionLimit;
import antifraud.utils.DecisionMap;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TransactionService {


    private final TransactionRepository transactionRepository;

    private final CreditCardService creditCardService;

    private final IpAddressService ipAddressService;

    private final TransactionLimitRepository transactionLimitRepository;

    public TransactionService(TransactionRepository transactionRepository, CreditCardService creditCardService, IpAddressService ipAddressService, TransactionLimitRepository transactionLimitRepository) {
        this.transactionRepository = transactionRepository;
        this.creditCardService = creditCardService;
        this.ipAddressService = ipAddressService;
        this.transactionLimitRepository = transactionLimitRepository;
    }

    public Optional<TransactionResponseDTO> assesTransaction(TransactionDTO transaction) {


        boolean isValidTransaction = isValidTransactionRequest(transaction);

        if (!isValidTransaction) {
            return Optional.empty();
        }
        TransactionHistory newTransaction = convertToEntity(transaction);

        TransactionLimit cardLimit = transactionLimitRepository.findTransactionLimitByCardNumber(transaction.getNumber()).orElseGet(() -> {
            TransactionLimit newCardLimit = new TransactionLimit();
            newCardLimit.setCardNumber(transaction.getNumber());
            return transactionLimitRepository.save(newCardLimit);
        });

        Long maxAllowed = cardLimit.getMaxAllowed();
        Long maxManual = cardLimit.getMaxManual();


        TransactionResult result = evaluateTransaction(newTransaction, maxAllowed, maxManual);
        List<String> info = determineInfo(newTransaction, result, maxAllowed, maxManual);
        newTransaction.setResult(result);

        saveTransaction(newTransaction);
        return Optional.of(new TransactionResponseDTO(result.name(), String.join(", ", info)));
    }

    public boolean isValidTransactionRequest(TransactionDTO transaction) {
        long amount = transaction.getAmount();
        String ipAddress = transaction.getIp();
        String cardNumber = transaction.getNumber();

        boolean isValidIp = ipAddressService.isIpAddressValid(ipAddress);
        boolean isValidCardNumber = creditCardService.validateCardNumber(cardNumber);
        Region region = transaction.getRegion();
        Date date = transaction.getDate();

        return isValidIp && isValidCardNumber && amount > 0 && !region.name().isEmpty() && date != null;
    }

    public TransactionHistory convertToEntity(TransactionDTO transactionDTO) {
        TransactionHistory transactionHistory = new TransactionHistory();
        transactionHistory.setAmount(transactionDTO.getAmount());
        transactionHistory.setIp(transactionDTO.getIp());
        transactionHistory.setNumber(transactionDTO.getNumber());
        transactionHistory.setRegion(transactionDTO.getRegion());
        transactionHistory.setDate(transactionDTO.getDate());
        return transactionHistory;
    }

    private TransactionResult evaluateTransaction(TransactionHistory transaction, Long maxAllowed, Long maxManual) {
        boolean isIpBlacklisted = ipAddressService.isIpAddressBlacklisted(transaction.getIp());
        boolean isCardBlacklisted = creditCardService.cardIsBlacklisted(transaction.getNumber());

        if (isIpBlacklisted || isCardBlacklisted) {
            return TransactionResult.PROHIBITED;
        }

        Calendar calendar = Calendar.getInstance(); // Gets a calendar using the default time zone and locale.
        calendar.setTime(transaction.getDate());
        calendar.add(Calendar.HOUR_OF_DAY, -1); // Subtracts one hour from the current time.
        Date oneHourAgo = calendar.getTime();


        Long regionCheck = transactionRepository.filterTransactionByRegion(transaction.getNumber(), transaction.getRegion().name(), oneHourAgo, transaction.getDate());


        Long ipCheck = transactionRepository.filterTransactionByIp(transaction.getNumber(), transaction.getIp(), oneHourAgo, transaction.getDate());


        if (ipCheck > 2 || regionCheck > 2) {
            return TransactionResult.PROHIBITED;
        }

        if (regionCheck == 2 || ipCheck == 2) {
            return TransactionResult.MANUAL_PROCESSING;
        }


        if (transaction.getAmount() <= maxAllowed) {
            return TransactionResult.ALLOWED;
        } else if (transaction.getAmount() <= maxManual) {
            return TransactionResult.MANUAL_PROCESSING;
        }
        return TransactionResult.PROHIBITED;
    }

    private List<String> determineInfo(TransactionHistory transaction, TransactionResult result, Long maxAllowed, Long maxManual) {
        Calendar calendar = Calendar.getInstance(); // Gets a calendar using the default time zone and locale.
        calendar.setTime(transaction.getDate());
        calendar.add(Calendar.HOUR_OF_DAY, -1); // Subtracts one hour from the current time.
        Date oneHourAgo = calendar.getTime();
        List<String> info = new ArrayList<>();
        if (ipAddressService.isIpAddressBlacklisted(transaction.getIp())) {
            info.add("ip");
        }
        if (creditCardService.cardIsBlacklisted(transaction.getNumber())) {
            info.add("card-number");
        }
        if (transactionRepository.filterTransactionByRegion(transaction.getNumber(), transaction.getRegion().name(), oneHourAgo, transaction.getDate()) >= 2) {
            info.add("region-correlation");
        }
        if (transactionRepository.filterTransactionByIp(transaction.getNumber(), transaction.getIp(), oneHourAgo, transaction.getDate()) >= 2) {
            info.add("ip-correlation");
        }

        if (transaction.getAmount() > maxAllowed) {
            if (transaction.getAmount() <= maxManual && result.equals(TransactionResult.MANUAL_PROCESSING) || transaction.getAmount() > maxManual && result.equals(TransactionResult.PROHIBITED)) {

                info.add("amount");
            }


        }
        if (info.isEmpty()) {
            info.add("none");
        }
        Collections.sort(info);
        return info;
    }

    public void saveTransaction(TransactionHistory transactionHistory) {
        transactionRepository.save(transactionHistory);

    }

    public List<TransactionHistoryResponseDTO> getAllTransactions() {
        List<TransactionHistoryResponseDTO> allTransactions = new ArrayList<>();

        transactionRepository.findAll().forEach(transactionHistory -> {
            allTransactions.add(new TransactionHistoryResponseDTO(transactionHistory));
        });

        return allTransactions;
    }

    public List<TransactionHistoryResponseDTO> getTransactionsByCardNumber(String number) {
        List<TransactionHistory> historyList = transactionRepository.getAllByNumber(number);

        List<TransactionHistoryResponseDTO> dtoList = new ArrayList<>();

        historyList.forEach(transactionHistory -> {
            dtoList.add(new TransactionHistoryResponseDTO(transactionHistory));
        });

        return dtoList;
    }

    @Transactional
    public ResponseEntity<TransactionHistoryResponseDTO> updateTransaction(TransactionFeedbackRequest request) {
        DecisionMap decisionMatrix = new DecisionMap();


        Optional<TransactionHistory> transaction = transactionRepository.findById(request.transactionId());

        if (transaction.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        if (transaction.get().isFeedBackProvided()) {
            return ResponseEntity.status(409).build();
        }

        TransactionLimit cardLimit = transactionLimitRepository.findTransactionLimitByCardNumber(transaction.get().getNumber()).orElseGet(
                () -> {
                    TransactionLimit newCardLimit = new TransactionLimit();
                    newCardLimit.setCardNumber(transaction.get().getNumber());
                    return transactionLimitRepository.save(newCardLimit);
                }
        );

        AdjustTransactionLimit adjuster = new AdjustTransactionLimit(cardLimit.getMaxAllowed(), cardLimit.getMaxManual());
        TransactionResult result = transaction.get().getResult();


        TransactionFeedback feedback = request.feedback();

        if (String.valueOf(result).equals(String.valueOf(feedback))) {
            return ResponseEntity.status(422).build();
        }

        Long amount = transaction.get().getAmount();

        TransactionLimitAction action = decisionMatrix.getAction(result, feedback);


        if (action != null) {
            action.apply(adjuster, amount);
        }

        cardLimit.setMaxManual(adjuster.getValues().maxManual);
        cardLimit.setMaxAllowed(adjuster.getValues().maxAllowed);


        transaction.get().setFeedback(request.feedback());
        transaction.get().setFeedBackProvided(true);

        TransactionHistory updatedHistory = transactionRepository.save(transaction.get());
        
        transactionLimitRepository.save(cardLimit);

        return ResponseEntity.ok(new TransactionHistoryResponseDTO(updatedHistory));


    }


}
