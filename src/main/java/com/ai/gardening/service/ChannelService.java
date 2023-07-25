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
    private final ChannelRepository channelRepository;
    private final AppUserRepository appUserRepository;
    private final AppUserService appUserService;
    private final TokenService tokenService;

    /**
     * Creates a new channel after checking 3 conditions: the name can not be null or used and the app user has to exist
     *
     * @param createChannelRequest is the DTO that contains the name of the new channel, with specific validations
     * @param token                is the token of the user that wants to create a new channel, in order for it to be assigned as the owner of the channel
     * @return the name of the newly created channel with 200 status or null as the name and 404 status if the channel could not be created
     */
    public ResponseEntity<ChannelResponse> createChannel(CreateChannelRequest createChannelRequest, String token) {
        AppUser appUser = appUserService.findCurrentAppUser(token);

        if (createChannelRequest.getName() == null) {
            LOGGER.info("The channel was not created because the name is null.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ChannelResponse(createChannelRequest.getName()));
        }

        if (findChannelByName(createChannelRequest.getName()).getId() == null && appUser.getId() != null) {

            Channel newChannel = new Channel(createChannelRequest.getName(), false, appUser);
            channelRepository.save(newChannel);
            addAppUserToChannel(appUser, newChannel);

            LOGGER.info("A new channel with name {} was saved.", createChannelRequest.getName());
            return ResponseEntity.status(HttpStatus.CREATED).body(new ChannelResponse(createChannelRequest.getName()));
        }

        LOGGER.info("A channel with name {} already exists or the user was not found. ", createChannelRequest.getName());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ChannelResponse());
    }

    /**
     * Finds all channels that a user created
     *
     * @param token is the authentication token assigned to a user that is used to find the user that makes this request
     * @return the list of channels' names
     */
    public ResponseEntity<List<ChannelResponse>> findAllOwnedChannels(String token) {
        AppUser appUser = appUserService.findCurrentAppUser(token);

        if (appUser.getId() != null) {
            List<Channel> channels = channelRepository.findAllByOwner(appUser);
            List<ChannelResponse> responseList = new ArrayList<>();

            channels.forEach(g -> responseList.add(new ChannelResponse(g.getName())));

            return ResponseEntity.status(HttpStatus.OK).body(responseList);
        }

        LOGGER.info("The app user was not found.");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
    }

    /**
     * Finds all channels that a user has joined - his own and other user's
     *
     * @param token is the authentication token assigned to a user that is used to find the user that makes this request
     * @return the list of channels' names
     */
    public ResponseEntity<List<ChannelResponse>> findAllJoinedChannels(String token) {
        AppUser appUser = appUserService.findCurrentAppUser(token);

        if (appUser.getId() != null) {
            List<Channel> channels = channelRepository.findAllByJoinedAppUsers(appUser);
            List<ChannelResponse> responseList = new ArrayList<>();

            channels.forEach(g -> responseList.add(new ChannelResponse(g.getName())));

            return ResponseEntity.status(HttpStatus.OK).body(responseList);
        }

        LOGGER.info("The app user was not found.");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
    }

    /**
     * Updates the name of a channel after checking 2 conditions: the channel has to exist and the person that makes the request has to be the owner of the channel
     *
     * @param updateChannelRequest is the DTO that contains the channel's id and the new name
     * @param token                is the authentication token assigned to a user that is used to find the user that makes this request
     * @return a string message that specifies if the name was changed or not, and status 200 for success or 404 if the name or owner were not found
     */
    public ResponseEntity<String> updateChannelName(UpdateChannelRequest updateChannelRequest, String token) {
        Optional<Channel> channel = channelRepository.findById(updateChannelRequest.getChannelId());

        if (channel.isPresent() && isAppUserTheOwner(channel.get().getOwner(), token)) {
            channel.get().setName(updateChannelRequest.getName());
            channelRepository.save(channel.get());
            LOGGER.info("The channel was renamed to {}", updateChannelRequest.getName());
            return ResponseEntity.status(HttpStatus.OK).body("The channel was renamed.");
        }

        LOGGER.info("The channel or owner was not found.");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("The channel or owner was not found.");
    }

    /**
     * Deletes an existing channel, after checking if the user that makes the request is the owner
     *
     * @param channelId is the id of the channel that has to be deleted
     * @param token     is the authentication token assigned to a user that is used to find the user that makes this request
     * @return a message that specifies if the channel was deleted or not, with a status 200 for success or 404 if the channel or user were not found
     */
    @Transactional
    public ResponseEntity<String> deleteChannel(long channelId, String token) {
        Optional<Channel> channel = channelRepository.findById(channelId);


        if (channel.isPresent()) {
            AppUser appUser = channel.get().getOwner();

            if (isAppUserTheOwner(appUser, token)) {
                appUserService.removeJoinedAppUserFromChannel(appUser, channel.get());
                channelRepository.delete(channel.get());
                LOGGER.info("The channel with id {} was deleted", channelId);
                return ResponseEntity.status(HttpStatus.OK).body("The channel was deleted.");
            }
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("The channel does not exist or the user is not the owner.");
    }

    /**
     * Adds a user to a new channel
     *
     * @param channelId is the id of the channel the user wants to join
     * @param token     is the authentication token assigned to a user that is used to find the user that makes this request
     * @return a message that specifies if the user joined or not, with a status 200 for success or 404 for channel or user were not found
     */
    public ResponseEntity<String> joinChannel(long channelId, String token) {
        AppUser appUser = appUserService.findCurrentAppUser(token);
        Optional<Channel> channel = channelRepository.findById(channelId);

        if (appUser.getId() != null && channel.isPresent()) {

            if (appUser.getJoinedChannels().stream().noneMatch(c -> c.equals(channel.get()))) {
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

    /**
     * Adds a user to a new channel - this method is used for the many-to-many relationship between AppUser and Channel
     *
     * @param appUser is the user that wants to join
     * @param channel is the channel that an user wants to join
     */
    public void addAppUserToChannel(AppUser appUser, Channel channel) {
        try {
            appUser.getJoinedChannels().add(channel);
            channel.getJoinedAppUsers().add(appUser);

            appUserRepository.save(appUser);
            channelRepository.save(channel);
        } catch (Exception e) { //TODO: change exception
            LOGGER.error("Failed to add app user to channel");
            throw e;
        }
    }

    /**
     * Finds a channel by its name
     *
     * @param channelName is the name of the channel that needs to be found
     * @return the channel that was found OR a new empty channel if none was found.
     */
    public Channel findChannelByName(String channelName) {
        Optional<Channel> channel = channelRepository.findByName(channelName);
        return channel.orElse(new Channel());
    }

    /**
     * Finds a channel by its id
     *
     * @param channelId is the id of the channel that needs to be found
     * @return the channel that was found OR a new empty channel if none was found.
     */
    public Channel findChannelById(long channelId) {
        Optional<Channel> channel = channelRepository.findById(channelId);

        return channel.isPresent() ? channel.get() : new Channel();
    }

    /**
     * Checks if a user is the owner of a channel, based on the authentication token that is received at every request
     * The method do not check specifically for channels, but its private access restricts its use for channels only
     *
     * @param appUser is the user for which the verification is made
     * @param token   is the authentication token that is used to verify if the user is the owner
     * @return true if the user is the owner OR false if not
     */
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
