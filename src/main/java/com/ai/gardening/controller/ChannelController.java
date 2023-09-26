package com.ai.gardening.controller;

import com.ai.gardening.dto.*;
import com.ai.gardening.service.ChannelService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/channel")
@AllArgsConstructor
public class ChannelController {

    private ChannelService channelService;

    @PostMapping(path = "/create")
    public ResponseEntity<ChannelResponse> createChannel(@RequestBody @Valid CreateChannelRequest createChannelRequest, @RequestHeader("Authorization") String token) {
        return channelService.createChannel(createChannelRequest, token);
    }

    @GetMapping(path = "/all")
    public ResponseEntity<List<AllChannelsResponse>> getAllChannels() {
        return channelService.findAllChannels();
    }

    @GetMapping(path = "/owned")
    public ResponseEntity<List<ChannelResponse>> getAllOwnedChannels(@RequestHeader("Authorization") String token) {
        return channelService.findAllOwnedChannels(token);
    }

    @GetMapping(path = "/all-joined-channels")
    public ResponseEntity<List<ChannelResponse>> getAllJoinedChannels(@RequestHeader("Authorization") String token) {
        return channelService.findAllJoinedChannels(token);
    }

    /**
     * Returns information about only one channel, based on channel's id
     * @param channelId the identification criteria for the channel
     * @param token app user's authentication token used to verify that an user is authenticated
     * @return a DTO that contains the information about the channel
     */
    @GetMapping(path = "/{channelId}")
    public ResponseEntity<GetChannelResponse> getChannel(@PathVariable("channelId") long channelId, @RequestHeader("Authorization") String token) {
        return channelService.getChannel(channelId, token);
    }

    @PutMapping(path = "/update")
    public ResponseEntity<String> renameChannel(@RequestBody @Valid UpdateChannelRequest updateChannelRequest, @RequestHeader("Authorization") String token) {
        return channelService.updateChannelName(updateChannelRequest, token);
    }

    @DeleteMapping(path = "/delete/{channelId}")
    public ResponseEntity<String> deleteChannel(@PathVariable("channelId") long channelId, @RequestHeader("Authorization") String token) {
        return channelService.deleteChannel(channelId, token);
    }

    @PostMapping(path = "/join/{channelId}")
    public ResponseEntity<String> joinChannel(@PathVariable("channelId") long channelId, @RequestHeader("Authorization") String token) {
        return channelService.joinChannel(channelId, token);
    }

//    @PostMapping(path = "/join/{channelId}")
//    public void exitChannel(@PathVariable("channelId") long channelId, @RequestHeader("Authorization") String token) {
//        //TODO: implement
//    }

    @GetMapping(path = "/joined-channels") //TODO: find a better path
    public ResponseEntity<List<Long>> findChannelIdsByUserId(@RequestHeader("Authorization") String token) {
        return channelService.findChannelIdsByUserId(token);
    }
}
