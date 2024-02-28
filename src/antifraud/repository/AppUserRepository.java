package antifraud.repository;

import antifraud.model.entity.AppUser;
import jakarta.transaction.Transactional;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface AppUserRepository extends CrudRepository<AppUser, Integer> {
    Optional<AppUser> findAppUserByUsername(String username);

    Optional<AppUser> findAppUserById(Long id);


    @Transactional
    int deleteAppUserByUsername(String username);


}
