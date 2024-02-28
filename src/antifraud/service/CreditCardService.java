package antifraud.service;

import antifraud.model.entity.CreditCard;
import antifraud.model.records.DeleteCardResponse;
import antifraud.repository.CreditCardRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CreditCardService {

    private final CreditCardRepository creditCardRepository;


    public CreditCardService(CreditCardRepository creditCardRepository) {
        this.creditCardRepository = creditCardRepository;
    }

    public boolean validateCardNumber(String number) {
        int checksum = Character.getNumericValue(number.charAt(number.length() - 1));
        int total = 0;

        for (int i = number.length() - 2; i >= 0; i--) {
            int sum = 0;
            int digit = Character.getNumericValue(number.charAt(i));
            if (i % 2 == number.length() % 2) { //right to left every odd digit
                digit = digit * 2;
            }

            sum = digit / 10 + digit % 10;
            total += sum;
        }

        return total % 10 != 0 ? 10 - total % 10 == checksum : checksum == 0;

    }


    public boolean cardIsBlacklisted(String card) {
        return creditCardRepository.findCreditCardByNumber(card).isPresent();
    }


    public CreditCard registerCard(String cardNumber) {
        CreditCard newCard = new CreditCard();
        newCard.setNumber(cardNumber);
        return creditCardRepository.save(newCard);

    }

    public List<CreditCard> getAllCards() {
        Iterable<CreditCard> cards = creditCardRepository.findAll();
        List<CreditCard> cardList = new ArrayList<>();

        for (CreditCard card : cards) {
            cardList.add(card);
        }

        return cardList;
    }

    @Transactional
    public DeleteCardResponse deleteCard(String number) {
        Optional<CreditCard> cardToDelete = creditCardRepository.findCreditCardByNumber(number);
        cardToDelete.ifPresent(creditCardRepository::delete);


        return new DeleteCardResponse("Card " + number + " successfully removed!");
    }

}
