package com.ai.gardening.service;

import com.ai.gardening.dtos.CreateChannelRequest;
import com.ai.gardening.dtos.UpdateChannelRequest;
import com.ai.gardening.dtos.ChannelResponse;
import com.ai.gardening.entity.AppUser;
import com.ai.gardening.entity.Channel;
import com.ai.gardening.entity.Token;
import com.ai.gardening.repository.AppUserRepository;
import com.ai.gardening.repository.ChannelRepository;
import com.ai.gardening.service.security.TokenService;
import lombok.AllArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class ChannelService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChannelService.class);
    private ChannelRepository channelRepository;
    private AppUserRepository appUserRepository;
    private AppUserService appUserService;
    private TokenService tokenService;

    public ResponseEntity<ChannelResponse> createChannel(CreateChannelRequest createChannelRequest, String token) {
        AppUser appUser = appUserService.findCurrentAppUser(token);

        if (findChannelByName(createChannelRequest.getTitle()).getId() == null && appUser.getId() != null) {
            Channel newChannel = new Channel(createChannelRequest.getTitle(), false, appUser);
            channelRepository.save(newChannel);
            addAppUserToChannel(appUser, newChannel);

            LOGGER.info("A new channel with name {} was saved.", createChannelRequest.getTitle());
            return ResponseEntity.status(HttpStatus.CREATED).body(new ChannelResponse(createChannelRequest.getTitle()));
        }

        LOGGER.info("A channel with name {} already exists or the user was not found. ", createChannelRequest.getTitle());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ChannelResponse());
    }

    public ResponseEntity<List<ChannelResponse>> getAllChannelsByUserId(String token) {
        AppUser appUser = appUserService.findCurrentAppUser(token);

        if (appUser.getId() != null) {
            List<Channel> groups = channelRepository.findAllByOwner(appUser);
            List<ChannelResponse> responseList = new ArrayList<>();

            groups.forEach(g -> responseList.add(new ChannelResponse(g.getName())));

            return ResponseEntity.status(HttpStatus.OK).body(responseList);
        }

        LOGGER.info("The app user was not found.");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
    }

    public ResponseEntity<String> renameChannel(UpdateChannelRequest updateChannelRequest, String token) {
        Optional<Channel> channel = channelRepository.findById(updateChannelRequest.getChannelId());

        if (channel.isPresent() && isAppUserTheOwner(channel.get().getOwner(), token)) {
            channel.get().setName(updateChannelRequest.getTitle());
            channelRepository.save(channel.get());
            LOGGER.info("The channel was renamed to {}", updateChannelRequest.getTitle());
            return ResponseEntity.status(HttpStatus.OK).body("The channel was renamed.");
        }

        LOGGER.info("The channel or owner was not found.");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("The channel or owner was not found.");
    }

    @Transactional
    public ResponseEntity<String> deleteChannel(long channelId, String token) {
        Optional<Channel> channel = channelRepository.findById(channelId);
        AppUser appUser = channel.get().getOwner();

        if (channel.isPresent() && isAppUserTheOwner(appUser, token)) {

            appUserService.removeJoinedAppUserFromChannel(appUser, channel.get());
            channelRepository.delete(channel.get());
            LOGGER.info("The channel with id {} was deleted", channelId);
            return ResponseEntity.status(HttpStatus.OK).body("The channel was deleted.");
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("The channel does not exist or the user is not the owner.");
    }

    public ResponseEntity<String> joinChannel(long channelId, String token) {
        AppUser appUser = appUserService.findCurrentAppUser(token);
        Optional<Channel> channel = channelRepository.findById(channelId);

        if (appUser.getId() != null && channel.isPresent()) {

            if (!appUser.getJoinedChannels().stream().anyMatch(c -> c.equals(channel.get()))) {
                addAppUserToChannel(appUser, channel.get());
                LOGGER.info("User with id {} joined the channel with id {}", appUser.getId(), channelId);
                return ResponseEntity.status(HttpStatus.OK).body("New user has joined to the channel.");
            }

            LOGGER.info("User with id {} already joined the channel with id {}", appUser.getId(), channelId);
            return ResponseEntity.status(HttpStatus.OK).body("User already has joined the channel.");
        }

        LOGGER.info("User or channel were not found. Could not join the channel.");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User or channel was not found. Use did not joined the channel.");
    }

    public void addAppUserToChannel(AppUser appUser, Channel channel) {
        try {
            appUser.getJoinedChannels().add(channel);
            channel.getJoinedAppUsers().add(appUser);

            appUserRepository.save(appUser);
            channelRepository.save(channel);
        } catch (Exception e) {
            LOGGER.error("Failed to add app user to channel");
            throw e;
        }
    }

    public Channel findChannelByName(String channelName) {
        Optional<Channel> channel = channelRepository.findByName(channelName);
        return channel.orElse(new Channel());
    }

    public Channel findChannelById(long channelId) {
        Optional<Channel> channel = channelRepository.findById(channelId);

        return channel.isPresent() ? channel.get() : new Channel();
    }

    private boolean isAppUserTheOwner(AppUser appUser, String token) {
        token = token.substring(7);
        Optional<Token> tokenOptional = tokenService.findByTokenAndUser(appUser, token);

        if (tokenOptional.isPresent()) {
            LOGGER.info("User with id {} is the admin of this post.", appUser.getId());
            return true;
        }

        LOGGER.info("User with id {} is the admin of this post.", appUser.getId());
        return false;
    }
}
