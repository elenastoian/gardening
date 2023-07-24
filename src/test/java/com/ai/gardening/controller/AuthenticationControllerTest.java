package com.ai.gardening.controller;

import com.ai.gardening.dtos.AuthenticationRequest;
import com.ai.gardening.dtos.AuthenticationResponse;
import com.ai.gardening.dtos.RegisterRequest;
import com.ai.gardening.service.security.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest {

    @InjectMocks
    private AuthenticationController authenticationController;

    @Mock
    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authenticationController = new AuthenticationController(authenticationService);
    }

    @Test
    void AuthenticationController_register_AuthenticationServiceIsCalledAndTheAuthenticationIsSuccessful() {

        RegisterRequest request = new RegisterRequest();
        // set request properties here

        AuthenticationResponse expectedResponse = new AuthenticationResponse();
        // set expected response properties here

        when(authenticationService.register(request)).thenReturn(new ResponseEntity<>(expectedResponse, HttpStatus.OK));

        ResponseEntity<AuthenticationResponse> responseEntity = authenticationController.register(request);
        AuthenticationResponse actualResponse = responseEntity.getBody();

        verify(authenticationService, times(1)).register(request);

        // assert the response
        assert responseEntity.getStatusCode().equals(HttpStatus.OK);
        assert actualResponse.equals(expectedResponse);

    }

    @Test
    void AuthenticationController_authenticate_AuthenticationServiceIsCalledAndTheAuthenticationIsSuccessful() {
        AuthenticationRequest request = new AuthenticationRequest();
        // set request properties here

        AuthenticationResponse expectedResponse = new AuthenticationResponse();
        // set expected response properties here

        when(authenticationService.authenticate(request)).thenReturn(new ResponseEntity<>(expectedResponse, HttpStatus.OK));

        ResponseEntity<AuthenticationResponse> responseEntity = authenticationController.authenticate(request);
        AuthenticationResponse actualResponse = responseEntity.getBody();

        verify(authenticationService, times(1)).authenticate(request);

        // assert the response
        assert responseEntity.getStatusCode().equals(HttpStatus.OK);
        assert actualResponse.equals(expectedResponse);
    }

}