package com.ai.gardening.service;

import com.ai.gardening.entity.AppUser;
import com.ai.gardening.entity.Channel;
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
     * Method finds the AppUser that has the given token assigned
     *
     * @param token is the token related to the AppUser
     * @return the AppUser that was found or a new empty AppUser if no user was found
     */
    @Transactional
    public AppUser findCurrentAppUser(String token) {
        token = token.substring(7);
        Optional<AppUser> appUserOptional = appUserRepository.findByTokensToken(token);

        if (appUserOptional.isPresent()) {
            return appUserOptional.get();
        }
        LOGGER.info("AppUser has not been found by the authentication token.");
        return new AppUser();
    }

    /**
     * Method removes the app user from a channel
     *
     * @param appUser is the user that is going to be removed from the channel
     * @param channel is the channel from which the user will be removed
     */
    public void removeJoinedAppUserFromChannel(AppUser appUser, Channel channel) {
        appUser.getJoinedChannels().remove(channel);
        channel.getJoinedAppUsers().remove(appUser);
    }
}
