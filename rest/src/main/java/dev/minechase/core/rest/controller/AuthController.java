package dev.minechase.core.rest.controller;

import dev.minechase.core.rest.model.Account;
import dev.minechase.core.rest.model.dto.AccountDTO;
import dev.minechase.core.rest.model.dto.AuthDtos;
import dev.minechase.core.rest.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AccountService accountService;
    private final JwtDecoder jwtDecoder;

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody AuthDtos.RegisterRequest request) {
        try {
            if (this.accountService.getAccountByEmail(request.email) != null) {
                return ResponseEntity.badRequest().body("An account with that email already exists");
            }
            if (this.accountService.getAccountByUsername(request.username) != null) {
                return ResponseEntity.badRequest().body("An account with that username already exists");
            }

            this.accountService.saveAccount(new Account(request));
            return ResponseEntity.ok("Registered successfully");
        } catch (Exception e) {
            throw new IllegalStateException("Error registering a new account: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody AuthDtos.LoginRequest request) {
        return this.accountService.loginAccount(request);
    }

    @PostMapping("/me")
    public ResponseEntity<AccountDTO> me(@Valid @RequestBody AuthDtos.MeRequest request) {
        Jwt jwt = this.jwtDecoder.decode(request.jwt);
        System.out.println("jwt: " + jwt.toString());
        Account account = this.accountService.getAccountByEmail(jwt.getSubject());

        if (account != null) return new ResponseEntity<>(account.toDTO(), HttpStatus.OK);

        return ResponseEntity.notFound().build();
    }
}
