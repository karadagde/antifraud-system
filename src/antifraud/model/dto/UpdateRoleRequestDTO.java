package antifraud.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UpdateRoleRequestDTO {
    @NotNull(message = "Role cannot be empty")
    private String role;
    @NotNull(message = "Username cannot be empty")
    private String username;

}
