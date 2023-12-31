package com.ai.gardening.controller;


import com.ai.gardening.dto.AuthenticationRequest;
import com.ai.gardening.dto.AuthenticationResponse;
import com.ai.gardening.dto.RegisterRequest;
import com.ai.gardening.dto.TokenConfirmationResponse;
import com.ai.gardening.service.security.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping(value = "/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request){
        return authenticationService.register(request);
    }

    // The token received as a response will be used in frontend
    @PostMapping(value = "/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request){
        return authenticationService.authenticate(request);
    }

    @GetMapping(path = "/confirm")
    public ResponseEntity<TokenConfirmationResponse> confirm(@RequestParam("token") String token){
        return authenticationService.confirmToken(token);
    }
}
