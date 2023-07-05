package com.ai.gardening.service;

import com.ai.gardening.dtos.AuthenticationRequest;
import com.ai.gardening.dtos.AuthenticationResponse;
import com.ai.gardening.dtos.RegisterRequest;
import com.ai.gardening.entity.AppUser;
import com.ai.gardening.entity.Token;
import com.ai.gardening.entity.enums.AppUserRole;
import com.ai.gardening.entity.enums.TokenType;
import com.ai.gardening.repository.AppUserRepository;
import com.ai.gardening.repository.TokenRepository;
import com.ai.gardening.service.security.EmailService;
import com.ai.gardening.service.security.EmailValidatorService;
import com.ai.gardening.service.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @InjectMocks
    private AuthenticationService authenticationService;

    @Mock
    private AppUserRepository appUserRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private EmailValidatorService emailValidatorService;

    @Mock
    private EmailService emailService;

    @Captor
    private ArgumentCaptor<Token> tokenCaptor;

    @Test
    void AuthenticationService_register_AppUserIsAlreadyRegistered() {
        // arrange
        RegisterRequest request = new RegisterRequest();
        request.setName("Test User");
        request.setEmail("test@example.com");
        request.setPassword("password");

        AppUser existingUser = new AppUser();
        existingUser.setId(1L);
        existingUser.setEmail("test@example.com");

        // act
        ResponseEntity<AuthenticationResponse> response = authenticationService.register(request);

        // assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertEquals("THE EMAIL IS NOT VALID", response.getBody().getToken());
    }

    @Test
    void AuthenticationService_saveUserToken_TokenIsSavedSuccessfully() throws Exception {
        // arrange
        AppUser savedAppUser = new AppUser();
        savedAppUser.setId(1L);

        String jwtToken = "testToken";

        // access the private method using reflection
        Method method = AuthenticationService.class.getDeclaredMethod("saveUserToken", AppUser.class, String.class);
        method.setAccessible(true);

        // act
        method.invoke(authenticationService, savedAppUser, jwtToken);

        // assert
        verify(tokenRepository).save(tokenCaptor.capture());
        Token savedToken = tokenCaptor.getValue();
        assertThat(savedToken.getUser()).isEqualTo(savedAppUser);
        assertThat(savedToken.getToken()).isEqualTo(jwtToken);
        assertThat(savedToken.getTokenType()).isEqualTo(TokenType.BEARER);
        assertThat(savedToken.isExpired()).isFalse();
        assertThat(savedToken.isRevoked()).isFalse();
    }

    @Test
    public void AuthenticationService_revokeAllUserTokens_AllTokensForAppUserAreRevoked() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // given
        Long userId = 1L;
        AppUser user = AppUser.builder()
                .id(userId)
                .name("testuser")
                .email("testpass")
                .appUserRole(AppUserRole.USER)
                .password("password").isAccountNonExpired(true).isAccountNonLocked(true).isCredentialsNonExpired(true).isEnabled(true).build();

        Token token1 = Token.builder()
                .token("token1")
                .tokenType(TokenType.BEARER)
                .user(user)
                .revoked(false)
                .expired(false).build();

        Token token2 = Token.builder()
                .token("token1")
                .tokenType(TokenType.BEARER)
                .user(user)
                .revoked(false)
                .expired(false).build();

        when(tokenRepository.findAllValidTokenByUser(userId)).thenReturn(List.of(token1, token2));

        // when
        // access the private method using reflection
        Method method = AuthenticationService.class.getDeclaredMethod("revokeAllUserTokens", AppUser.class);
        method.setAccessible(true);
        method.invoke(authenticationService, user);

        // then
        verify(tokenRepository, times(1)).saveAll(List.of(token1, token2));
        assertTrue(token1.isExpired());
        assertTrue(token1.isRevoked());
        assertTrue(token2.isExpired());
        assertTrue(token2.isRevoked());
    }

    @Test
    public void AuthenticationService_authenticate_AppUserAuthenticationRequestIsValid() {
        // given
        AuthenticationRequest authRequest = new AuthenticationRequest("testuser@example.com", "testpass");

        Long userId = 1L;
        AppUser user = AppUser.builder()
                .id(userId)
                .name("testuser")
                .email("testpass")
                .appUserRole(AppUserRole.USER)
                .password("password").isAccountNonExpired(true).isAccountNonLocked(true).isCredentialsNonExpired(true).isEnabled(true).build();
        when(appUserRepository.findByEmail(authRequest.getEmail())).thenReturn(Optional.of(user));
       // when(passwordEncoder.matches(authRequest.getPassword(), user.getPassword())).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn("testtoken");
       // when(appUserRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        // when
        ResponseEntity<AuthenticationResponse> response = authenticationService.authenticate(authRequest);

        // then
        verify(tokenRepository, times(1)).save(any(Token.class));
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("testtoken", response.getBody().getToken());
    }

    @Test
    public void AuthenticationService_authenticate_AppUserAuthenticationRequestIsNotValid() {
        // given
        AuthenticationRequest authRequest = new AuthenticationRequest("testuser@example.com", "testpass");
        when(appUserRepository.findByEmail(authRequest.getEmail())).thenReturn(Optional.empty());

        // when
        ResponseEntity<AuthenticationResponse> response = authenticationService.authenticate(authRequest);

        // then
        verify(tokenRepository, never()).save(any(Token.class));
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("THE EMAIL OR PASSWORD IS NOT VALID", response.getBody().getToken());
    }

}