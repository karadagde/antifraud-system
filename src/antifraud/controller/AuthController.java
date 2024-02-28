package antifraud.controller;

import antifraud.model.dto.RegistrationRequestDTO;
import antifraud.model.dto.RegistrationResponseDTO;
import antifraud.model.dto.UpdateRoleRequestDTO;
import antifraud.model.entity.AppUser;
import antifraud.model.enums.Operation;
import antifraud.model.enums.Roles;
import antifraud.model.records.DeleteUserResponse;
import antifraud.model.records.LockUserOperation;
import antifraud.model.records.LockUserOperationResponse;
import antifraud.repository.AppUserRepository;
import antifraud.service.AppUserDetailsServiceImpl;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@Validated
public class AuthController {

    private final AppUserRepository repository;

    private final AppUserDetailsServiceImpl userDetailsService;

    public AuthController(AppUserRepository repository, AppUserDetailsServiceImpl userDetailsService) {
        this.repository = repository;
        this.userDetailsService = userDetailsService;
    }


    @PostMapping("/auth/user")
    public ResponseEntity<RegistrationResponseDTO> registerUser(@Valid @RequestBody RegistrationRequestDTO request) {


        try {
            Optional<AppUser> newUser = userDetailsService.registerUser(request);

            return newUser.map(appUser -> ResponseEntity.status(201).body(new RegistrationResponseDTO(appUser))).orElseGet(() -> ResponseEntity.status(409).build());


        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }


    }


    @GetMapping("/auth/list")

    public ResponseEntity<List<RegistrationResponseDTO>> getAllUsers() {
        return ResponseEntity.ok(userDetailsService.getAllUsers());

    }


    @DeleteMapping("/auth/user/{username}")
    public ResponseEntity<DeleteUserResponse> deleteUser(@PathVariable String username) throws IOException {

        Optional<AppUser> existingUser = repository.findAppUserByUsername(username);
        if (existingUser.isPresent()) {


            int result = userDetailsService.deleteUserByUsername(username);

            if (result == 1) {

                return ResponseEntity.ok(new DeleteUserResponse(username, "Deleted successfully!"));
            } else {
                throw new IOException("Something went wrong");
            }
        } else {
            return ResponseEntity.notFound().build();
        }

    }

    @Transactional
    @PutMapping("/auth/role")
    public ResponseEntity<RegistrationResponseDTO> changeUserRole(@Valid @RequestBody UpdateRoleRequestDTO request) {
        String userRole = request.getRole().toUpperCase();

        if (!userRole.equals(Roles.MERCHANT.name()) && !userRole.equals(Roles.SUPPORT.name())) {
            return ResponseEntity.badRequest().build();
        }

        Optional<AppUser> user = repository.findAppUserByUsername(request.getUsername());

        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else if (user.get().getAuthority().name().equals(userRole)) {
            return ResponseEntity.status(409).build();
        } else {
            user.get().setAuthority(Roles.valueOf(userRole));
            AppUser updatedUser = repository.save(user.get());
            return ResponseEntity.ok(new RegistrationResponseDTO(updatedUser));
        }

    }

    @Transactional
    @PutMapping("/auth/access")
    public ResponseEntity<LockUserOperationResponse> changeUserLock(@Valid @RequestBody LockUserOperation request) {
        String username = request.username();
        Operation op = request.operation();

        Optional<AppUser> user = repository.findAppUserByUsername(username);
        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        if (user.get().getAuthority().equals(Roles.ADMINISTRATOR)) {
            return ResponseEntity.badRequest().build();
        }

        AppUser updatedUser = user.get();

        if (op.equals(Operation.LOCK)) {
            updatedUser.setLocked(true);
            repository.save(updatedUser);
            String message = "User " + updatedUser.getUsername() + " locked!";
            return ResponseEntity.ok(new LockUserOperationResponse(message));
        } else if (op.equals(Operation.UNLOCK)) {
            updatedUser.setLocked(false);
            repository.save(updatedUser);
            String message = "User " + updatedUser.getUsername() + " unlocked!";

            return ResponseEntity.ok(new LockUserOperationResponse(message));
        } else {
            return ResponseEntity.badRequest().build();
        }
    }
}



