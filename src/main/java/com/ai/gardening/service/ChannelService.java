package com.ai.gardening.service;

import com.ai.gardening.dtos.ChannelRequest;
import com.ai.gardening.dtos.ChannelResponse;
import com.ai.gardening.entity.AppUser;
import com.ai.gardening.entity.Channel;
import com.ai.gardening.repository.AppUserRepository;
import com.ai.gardening.repository.ChannelRepository;
import lombok.AllArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

import org.slf4j.Logger;

@Service
@AllArgsConstructor
public class ChannelService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChannelService.class);
    private ChannelRepository channelRepository;
    private AppUserRepository appUserRepository;

    public ResponseEntity<String> createChannel(ChannelRequest channelRequest) {
        if (channelRequest == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The channel that was sent is null.");

        Optional<AppUser> appUser = appUserRepository.findById(channelRequest.getCreatorId());

        if (findChannel(channelRequest.getName()) == null) {
            Channel newChannel = new Channel(channelRequest.getName(), false, appUser.orElse(new AppUser())); //TODO: handle this exception
            channelRepository.save(newChannel);
            addAppUserToChannel(appUser.get(), newChannel);

            LOGGER.info("A new channel with name {} was saved.", newChannel.getName());
            return ResponseEntity.status(HttpStatus.CREATED).body("The new channel was created.");
        }

        LOGGER.info("A new channel with name {} already exists.", channelRequest.getName());
        return ResponseEntity.status(HttpStatus.OK).body("A channel with the same name already exists.");
    }

    public Channel findChannel(String channelName) {
        return channelRepository.findByName(channelName);
    }

    public ResponseEntity<List<ChannelResponse>> getAllChannelsByUserId(int userId) {
        List<Channel> groups = channelRepository.findAllByAppUserId(userId);

        if (groups.isEmpty()) {
            LOGGER.info("No group was found for user {}", userId);
            return ResponseEntity.status(HttpStatus.OK).body(Collections.emptyList());
        }

        List<ChannelResponse> responseList = new ArrayList<>();
        groups.forEach(g -> responseList.add(new ChannelResponse(g.getName())));

        return ResponseEntity.status(HttpStatus.OK).body(responseList);
    }

    public ResponseEntity<String> renameChannel(long channelId, String newName) {
        Optional<Channel> channel = channelRepository.findById(channelId);

        if (channel.isPresent()) {
            channel.get().setName(newName);
            channelRepository.save(channel.get());
            LOGGER.info("The channel was renamed to {}", newName);
            return ResponseEntity.status(HttpStatus.OK).body("The channel was renamed.");
        }

        return ResponseEntity.status(HttpStatus.OK).body("The channel was not found.");
    }

    //TODO: find a better solution for authentication check
    public ResponseEntity<String> deleteChannel(long channelId, long creatorId) {
        Optional<Channel> channel = channelRepository.findById(channelId);

        if (channel.isPresent()) {

            if (channel.get().getAdmin().getId().equals(creatorId)) {
                channelRepository.delete(channel.get());
                LOGGER.info("The channel with id {} was deleted", channelId);
                return ResponseEntity.status(HttpStatus.OK).body("The channel was deleted.");
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Only the creator can delete the channel");
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body("The channel does not exist.");
    }

    public ResponseEntity<String> joinChannel(long appUserId, long channelId) {
        Optional<AppUser> appUser = appUserRepository.findById(appUserId); //TODO: make it a separate method in AppUserService
        Optional<Channel> channel = channelRepository.findById(channelId); //TODO: make it a separate method

        if (appUser.isPresent() && channel.isPresent()) {
            addAppUserToChannel(appUser.get(), channel.get());
            return ResponseEntity.status(HttpStatus.OK).body("New user has joined to the channel.");
        }

        return ResponseEntity.status(HttpStatus.OK).body("User or channel was not found. No one could join.");
    }

    public void addAppUserToChannel(AppUser appUser, Channel channel) {
        try {
            appUser.getChannels().add(channel);
            channel.getUsers().add(appUser);

            appUserRepository.save(appUser);
            channelRepository.save(channel);
        } catch (Exception e) {
            LOGGER.error("Failed to add app user to channel");
            throw e;
        }
    }
}
