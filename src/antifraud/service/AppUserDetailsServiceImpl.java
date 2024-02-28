package antifraud.service;

import antifraud.model.dto.AppUserDTO;
import antifraud.model.dto.RegistrationRequestDTO;
import antifraud.model.dto.RegistrationResponseDTO;
import antifraud.model.entity.AppUser;
import antifraud.model.enums.Roles;
import antifraud.repository.AppUserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AppUserDetailsServiceImpl implements UserDetailsService {


    private final AppUserRepository repository;
    private final PasswordEncoder passwordEncoder;


    public AppUserDetailsServiceImpl(AppUserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser user = repository.findAppUserByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Not found"));
        return new AppUserDTO(user);
    }

    public List<RegistrationResponseDTO> getAllUsers() {
        List<RegistrationResponseDTO> allUsers = new ArrayList<>();

        repository.findAll().forEach(user -> {
            allUsers.add(new RegistrationResponseDTO(user));
        });

        return allUsers;

    }

    public int deleteUserByUsername(String username) {
        return repository.deleteAppUserByUsername(username);
    }

    public Optional<AppUser> registerUser(RegistrationRequestDTO request) {
        Optional<AppUser> existingUser = repository.findAppUserByUsername(request.getUsername());

        if (existingUser.isPresent()) {
            return Optional.empty();
        }

        boolean isFirstUser = repository.count() == 0;
        Roles role = isFirstUser ? Roles.ADMINISTRATOR : Roles.MERCHANT;
        boolean isLocked = !isFirstUser;


        AppUser user = new AppUser();
        user.setAuthority(role);
        user.setLocked(isLocked);
        user.setName(request.getName());
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));


        return Optional.of(repository.save(user));

    }

}
