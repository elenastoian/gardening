package com.ai.gardening.service;

import com.ai.gardening.entity.AppUser;
import com.ai.gardening.repository.AppUserRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@AllArgsConstructor
public class AppUserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AppUserService.class);
    private final AppUserRepository appUserRepository;

    /**
     * Method finds the AppUser that has the given token
     *
     * @param token is the token related to the AppUser
     * @return the AppUser that was found or a new empty AppUser
     */
    @Transactional
    public AppUser findCurrentAppUser(String token) {
        token = token.substring(7);
        Optional<AppUser> appUserOptional = appUserRepository.findByTokensToken(token);

        if(appUserOptional.isPresent()) {
            return appUserOptional.get();
        }
        LOGGER.info("AppUser has not been found by the authentication token.");
        return null;
    }
}
