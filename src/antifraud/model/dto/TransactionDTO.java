package antifraud.model.dto;


import antifraud.model.enums.Region;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Setter
@Getter
@ToString
public class TransactionDTO {

    @NotNull(message = "The amount cannot be null")
    @Min(value = 1, message = "The amount must be greater than 0")
    private Long amount;

    @NotNull(message = "Ip cannot be null")
    private String ip;

    @NotNull(message = "Number cannot be null")
    private String number;

    @NotNull(message = "Region cannot be null")
    @Enumerated(EnumType.STRING)
    private Region region;


    @NotNull(message = "Date cannot be null")
    private Date date;


}

