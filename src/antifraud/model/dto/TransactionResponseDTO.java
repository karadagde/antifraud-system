package antifraud.model.dto;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TransactionResponseDTO {

    private String result;
    private String info;


    public TransactionResponseDTO(String result, String info) {
        this.result = result;
        this.info = info;
    }

}

