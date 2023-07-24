package com.ai.gardening.service.security;

import com.ai.gardening.entity.AppUser;
import com.ai.gardening.entity.Token;
import com.ai.gardening.repository.TokenRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class TokenService {
    private final TokenRepository tokenRepository;


    public Optional<Token> findByTokenAndUser(AppUser appUser, String token) {

        return tokenRepository.findByTokenAndUser(token, appUser);
    }
}
