package com.ai.gardening.service;

import com.ai.gardening.dtos.AuthenticationRequest;
import com.ai.gardening.dtos.AuthenticationResponse;
import com.ai.gardening.dtos.RegisterRequest;
import com.ai.gardening.dtos.TokenConfirmationResponse;
import com.ai.gardening.entity.AppUser;
import com.ai.gardening.entity.ConfirmationToken;
import com.ai.gardening.entity.Token;
import com.ai.gardening.entity.enums.AppUserRole;
import com.ai.gardening.entity.enums.TokenType;
import com.ai.gardening.repository.AppUserRepository;
import com.ai.gardening.repository.TokenRepository;
import com.ai.gardening.service.security.ConfirmationTokenService;
import com.ai.gardening.service.security.EmailService;
import com.ai.gardening.service.security.EmailValidatorService;
import com.ai.gardening.service.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    final String EMAIL_IS_ALREADY_USED_ERROR = "THE EMAIL IS ALREADY USED";
    final String EMAIL_IS_NOT_VALID_ERROR = "THE EMAIL IS NOT VALID";

    final String EMAIL_OR_PASSWORD_IS_NOT_VALID_ERROR = "THE EMAIL OR PASSWORD IS NOT VALID";

    final String EMAIL_IS_NOT_ENABLED_ERROR = "THE EMAIL IS NOT ENABLED";


    //TODO: change it to port 4200 when the frontend will be implemented
    @Value("${frontend_host_url}")
    private String frontendHostURL;

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final TokenRepository tokenRepository;
    private final EmailValidatorService emailValidatorService;
    private final EmailService emailService;

    private final ConfirmationTokenService confirmationTokenService;

    @Transactional
    public ResponseEntity<AuthenticationResponse> register(RegisterRequest request) {

        if (!emailValidatorService.testIfEmailIsValid(request.getEmail())) {
            return ResponseEntity.status(HttpStatus.OK).body(AuthenticationResponse.builder().token(EMAIL_IS_NOT_VALID_ERROR).build());
        }

        if(appUserRepository.findByEmail(request.getEmail()).isPresent()){
            return ResponseEntity.status(HttpStatus.OK).body(AuthenticationResponse.builder().token(EMAIL_IS_ALREADY_USED_ERROR).build());
        }

        AppUser appUser = AppUser.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .isAccountNonExpired(true)
                .isAccountNonLocked(true)
                .isCredentialsNonExpired(true)
                .isEnabled(false)
                .appUserRole(AppUserRole.USER)
                .build();

        AppUser savedAppUser = appUserRepository.save(appUser);

        String jwtToken = jwtService.generateToken(appUser);

        saveUserToken(savedAppUser, jwtToken);

        String token = UUID.randomUUID().toString();

        ConfirmationToken confirmationToken = new ConfirmationToken(token, LocalDateTime.now(), LocalDateTime.now().plusMinutes(15), appUser);

        confirmationTokenService.saveConfirmationToken(confirmationToken);

        final String link = frontendHostURL + "/email-confirmation/?token=" + token;

        this.emailService.send(appUser.getEmail(), emailService.buildEmail(appUser.getName(), link));

        return ResponseEntity.status(HttpStatus.OK).body(AuthenticationResponse.builder().token(jwtToken).build());
    }

    private void saveUserToken(AppUser savedAppUser, String jwtToken) {
        Token token = Token.builder()
                .user(savedAppUser)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();

        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(AppUser user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    public ResponseEntity<AuthenticationResponse> authenticate(AuthenticationRequest request) {

        if(!appUserRepository.findByEmail(request.getEmail()).isPresent()){
            return ResponseEntity.status(HttpStatus.OK).body(AuthenticationResponse.builder()
                    .token(EMAIL_OR_PASSWORD_IS_NOT_VALID_ERROR)
                    .build());
        }

        if(!appUserRepository.findByEmail(request.getEmail()).get().getIsEnabled()){
            return ResponseEntity.status(HttpStatus.OK).body(AuthenticationResponse.builder()
                    .token(EMAIL_IS_NOT_ENABLED_ERROR)
                    .build());
        }

        authenticationManager.authenticate( new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        //TODO: throw the correct exception and handle it
        AppUser appUser = appUserRepository.findByEmail(request.getEmail()).orElseThrow();

        String jwtToken = jwtService.generateToken(appUser);
        saveUserToken(appUser, jwtToken);

        return ResponseEntity.status(HttpStatus.OK).body(AuthenticationResponse.builder()
                .token(jwtToken)
                .build());
    }

    public ResponseEntity<TokenConfirmationResponse> confirmToken(String token) {

        Optional<ConfirmationToken> tokenFound;
        boolean tokenExists = confirmationTokenService.getToken(token).isPresent();

        if (tokenExists) {

            tokenFound = confirmationTokenService.getToken(token);
            ConfirmationToken tokenObject = tokenFound.get();

            if (tokenObject.getConfirmedAt() != null) {
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new TokenConfirmationResponse(
                        false
                ));
            }

            LocalDateTime expiredAt = tokenObject.getExpiresAt();

            if (expiredAt.isBefore(LocalDateTime.now())) {
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new TokenConfirmationResponse(
                        false
                ));
            }

            Integer confirmed = confirmationTokenService.setConfirmedAt(token);
            Integer enabled = appUserRepository.enableAppUser(tokenObject.getAppUser().getEmail());

            if ((confirmed == 1) && (enabled == 1)) {
                return ResponseEntity.status(HttpStatus.OK).body(new TokenConfirmationResponse(
                        true
                ));
            }

        } else {
            return ResponseEntity.status(HttpStatus.OK).body(new TokenConfirmationResponse(
                    false
            ));
        }
        return ResponseEntity.status(HttpStatus.OK).body(new TokenConfirmationResponse(
                false
        ));

    }
}
