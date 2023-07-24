package com.ai.gardening.controller;

import com.ai.gardening.dtos.CreateChannelRequest;
import com.ai.gardening.dtos.UpdateChannelRequest;
import com.ai.gardening.dtos.ChannelResponse;
import com.ai.gardening.service.ChannelService;
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
    public ResponseEntity<ChannelResponse> createChannel(@RequestBody CreateChannelRequest createChannelRequest, @RequestHeader("Authorization") String token) {
        return channelService.createChannel(createChannelRequest, token);
    }

    @GetMapping(path = "/get-all")
    public ResponseEntity<List<ChannelResponse>> getAllGroupsByUserId(@RequestHeader("Authorization") String token) {
        return channelService.getAllOwnedChannelsByUserId(token);
    }

    @PutMapping(path = "/update")
    public ResponseEntity<String> renameChannel(@RequestBody UpdateChannelRequest updateChannelRequest, @RequestHeader("Authorization") String token) {
        return channelService.renameChannel(updateChannelRequest, token);
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
