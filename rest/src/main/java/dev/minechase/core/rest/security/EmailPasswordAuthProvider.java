package dev.minechase.core.rest.security;

import dev.minechase.core.rest.model.Account;
import dev.minechase.core.rest.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
@RequiredArgsConstructor
public class EmailPasswordAuthProvider implements AuthenticationProvider {

  private final AccountRepository accountRepository;
  private final BCryptPasswordEncoder passwordEncoder;

  @Override
  public Authentication authenticate(Authentication authentication) {
    String email = (String) authentication.getPrincipal();
    String raw = (String) authentication.getCredentials();
    Account account = this.accountRepository.findByEmail(email);

    if (account == null) {
      throw new BadCredentialsException("Bad credentials");
    }

    if (!passwordEncoder.matches(raw, account.getPassword())) {
      throw new BadCredentialsException("Bad credentials");
    }

//    var authorities = account.getRoles().stream()
//        .map(r -> new SimpleGrantedAuthority(r.getName()))
//        .toList();

    return new UsernamePasswordAuthenticationToken(email, null, Collections.emptyList());
  }

  @Override
  public boolean supports(Class<?> auth) {
    return UsernamePasswordAuthenticationToken.class.isAssignableFrom(auth);
  }

}
