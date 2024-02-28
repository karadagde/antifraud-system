package antifraud.model.entity;

import antifraud.model.enums.Roles;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@Entity
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String name;
    private String username;
    private String password;

    @Enumerated(EnumType.STRING)
    private Roles authority;


    private Boolean isLocked;


    public Boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }
}
