package com.example.javaangularbe.user;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static com.example.javaangularbe.JwtTokenFilter.ISSUER;
import static com.example.javaangularbe.JwtTokenFilter.SECRET;


@RestController
@RequiredArgsConstructor
public class UserApi {

    private final AuthenticationManager authenticationManager;

    @PostMapping("/auth/login")
    public ResponseEntity<AuthResponse> getJwt(@RequestBody AuthRequest authRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword()));

            User principal = (User) authentication.getPrincipal();

            System.out.println(principal);

            String token = JWT.create()
                    .withSubject(principal.getUsername())
                    .withIssuer(ISSUER)
                    .withClaim("roles", principal.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                    .sign(Algorithm.HMAC256(SECRET));

            return ResponseEntity.ok(createResponse(principal.getUsername(), token));
        } catch (UsernameNotFoundException exception) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            System.out.printf(e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    private static AuthResponse createResponse(String email, String token) {
        return new AuthResponse(email, token);
    }
}
