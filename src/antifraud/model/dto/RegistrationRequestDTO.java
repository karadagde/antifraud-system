package antifraud.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class RegistrationRequestDTO {
    @NotNull(message = "Name cannot be empty")
    private String name;
    @NotNull(message = "Username cannot be empty")
    private String username;
    @NotNull(message = "Password cannot be empty")
    private String password;

    @NotNull
    public String getName() {
        return name;
    }


}
