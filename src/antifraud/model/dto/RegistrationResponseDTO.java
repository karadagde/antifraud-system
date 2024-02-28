package antifraud.model.dto;

import antifraud.model.entity.AppUser;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class RegistrationResponseDTO {
    @NotNull
    final String role;
    @NotNull
    private final String name;
    @NotNull
    private final String username;
    @NotNull
    private final Long id;

    public RegistrationResponseDTO(AppUser user) {
        this.name = user.getName();
        this.username = user.getUsername();
        this.id = user.getId();
        this.role = String.valueOf(user.getAuthority());
    }

}
