package com.ai.gardening.controller;

import com.ai.gardening.dtos.ChannelRequest;
import com.ai.gardening.dtos.ChannelResponse;
import com.ai.gardening.service.GroupService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/channel")
@AllArgsConstructor
public class ChannelController {

    private GroupService groupService;

    @PostMapping(path = "/create")
    public ResponseEntity<String> createChannel(@RequestBody ChannelRequest channelRequest) {
         return groupService.createChannel(channelRequest);
    }

    @GetMapping(path = "/{userId}")
    public ResponseEntity<List<ChannelResponse>> getAllGroupsByUserId(@PathVariable("userId") int userId) {
        return groupService.getAllChannelsByUserId(userId);
    }

    @PutMapping(path = "/rename/{channelId}/{name}")
    public ResponseEntity<String> renameChannel(@PathVariable("channelId") long channelId, @PathVariable("name") String newName) {
        return groupService.renameChannel(channelId, newName);
    }

    @DeleteMapping(path = "/delete/{channelId}/{creatorId}")
    public ResponseEntity<String> deleteChannel(@PathVariable("channelId") long channelId, @PathVariable("creatorId") long creatorId) {
        return groupService.deleteChannel(channelId, creatorId);
    }
}
