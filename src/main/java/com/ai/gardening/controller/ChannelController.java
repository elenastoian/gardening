package com.ai.gardening.controller;

import com.ai.gardening.dtos.CreateChannelRequest;
import com.ai.gardening.dtos.UpdateChannelRequest;
import com.ai.gardening.dtos.ChannelResponse;
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

    @GetMapping(path = "/get/owned")
    public ResponseEntity<List<ChannelResponse>> getAllOwnedChannels(@RequestHeader("Authorization") String token) {
        return channelService.findAllOwnedChannels(token);
    }

    @GetMapping(path = "/get/joined")
    public ResponseEntity<List<ChannelResponse>> getAllJoinedChannels(@RequestHeader("Authorization") String token) {
        return channelService.findAllJoinedChannels(token);
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
}
