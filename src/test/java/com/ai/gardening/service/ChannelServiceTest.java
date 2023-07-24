package com.ai.gardening.service;

import com.ai.gardening.dtos.ChannelResponse;
import com.ai.gardening.dtos.CreateChannelRequest;
import com.ai.gardening.dtos.UpdateChannelRequest;
import com.ai.gardening.entity.AppUser;
import com.ai.gardening.entity.Channel;
import com.ai.gardening.entity.Token;
import com.ai.gardening.repository.AppUserRepository;
import com.ai.gardening.repository.ChannelRepository;
import com.ai.gardening.service.security.TokenService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChannelServiceTest {

    @InjectMocks
    private ChannelService channelService;
    @Mock
    private ChannelRepository channelRepository;
    @Mock
    private AppUserRepository appUserRepository;
    @Mock
    private AppUserService appUserService;
    @Mock
    private TokenService tokenService;

    @Test
    void testCreateChannel_Success() {
        AppUser appUser = new AppUser();
        appUser.setId(1L);

        when(appUserService.findCurrentAppUser(anyString())).thenReturn(appUser);
        when(channelRepository.findByName(anyString())).thenReturn(Optional.empty());

        CreateChannelRequest createChannelRequest = new CreateChannelRequest("Channel Test Name");

        ResponseEntity<ChannelResponse> response = channelService.createChannel(createChannelRequest, "token");

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(createChannelRequest.getName(), response.getBody().getName());
    }

    @Test
    void testCreateChannel_ChannelExists() {
        AppUser appUser = new AppUser();
        when(appUserService.findCurrentAppUser(anyString())).thenReturn(appUser);

        Channel existingChannel = new Channel();
        when(channelRepository.findByName(anyString())).thenReturn(Optional.of(existingChannel));

        CreateChannelRequest createChannelRequest = new CreateChannelRequest("Channel Test Name");

        ResponseEntity<ChannelResponse> response = channelService.createChannel(createChannelRequest, "token");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testGetAllOwnedChannelsByUserId_Success() {
        AppUser appUser = new AppUser();
        appUser.setId(1L);

        List<Channel> channelList = new ArrayList<>();
        Channel channel1 = new Channel();
        channel1.setId(1L);
        channel1.setName("Name1");

        Channel channel2 = new Channel();
        channel2.setId(2L);
        channel2.setName("Name2");

        channelList.add(channel1);
        channelList.add(channel2);

        appUser.setOwnedChannels(channelList);

        when(appUserService.findCurrentAppUser(any(String.class))).thenReturn(appUser);

        ResponseEntity<List<ChannelResponse>> response = channelService.getAllOwnedChannelsByUserId("token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals(channelList.size(), response.getBody().size());
//
//        for (int i = 0; i < response.getBody().size(); i++) {
//            assertEquals(channelList.get(i).getName(), response.getBody().get(i).getName());
//        }
    }

    @Test
    public void testRenameChannel_Success() {
        AppUser appUser = new AppUser();
        appUser.setId(1L);

        Channel channel = new Channel();
        channel.setId(2L);
        channel.setName("Old Channel Name");
        channel.setOwner(appUser);

        String newChannelName = "New Channel Name";

        UpdateChannelRequest updateChannelRequest = new UpdateChannelRequest(2L, newChannelName);

        when(channelRepository.findById(any(Long.class))).thenReturn(Optional.of(channel));
        when(tokenService.findByTokenAndUser(any(AppUser.class), any(String.class))).thenReturn(Optional.of(new Token()));

        ResponseEntity<String> responseEntity = channelService.renameChannel(updateChannelRequest, "Bearer .eyJzdWIiOiJlbGVuYXN0b2lhbjAwQGdtYWlsLmNvbSIsImlhdCI6MTY5MDE4OTY0MiwiZXhwIjoxNjkwMTkxMDgyfQ.AyDyj2FXhSQvd3Gh4LHkdU1nxLRkJcU-xUUj3WUO4ew");

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(channel.getName(), newChannelName);
    }

    @Test
    public void testRenameChannel_ChannelDoesNotExist() {
        AppUser appUser = new AppUser();
        appUser.setId(1L);

        String newChannelName = "New Channel Name";

        UpdateChannelRequest updateChannelRequest = new UpdateChannelRequest(2L, newChannelName);

        when(channelRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        ResponseEntity<String> responseEntity = channelService.renameChannel(updateChannelRequest, "Bearer .eyJzdWIiOiJlbGVuYXN0b2lhbjAwQGdtYWlsLmNvbSIsImlhdCI6MTY5MDE4OTY0MiwiZXhwIjoxNjkwMTkxMDgyfQ.AyDyj2FXhSQvd3Gh4LHkdU1nxLRkJcU-xUUj3WUO4ew");

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    public void testRenameChannel_AppUserIsNotTheOwner() {
        AppUser appUser = new AppUser();
        appUser.setId(1L);

        Channel channel = new Channel();
        channel.setId(2L);
        channel.setName("Old Channel Name");
        channel.setOwner(new AppUser());

        String newChannelName = "New Channel Name";

        UpdateChannelRequest updateChannelRequest = new UpdateChannelRequest(2L, newChannelName);

        when(channelRepository.findById(any(Long.class))).thenReturn(Optional.of(channel));
        when(tokenService.findByTokenAndUser(any(AppUser.class), any(String.class))).thenReturn(Optional.empty());

        ResponseEntity<String> responseEntity = channelService.renameChannel(updateChannelRequest, "Bearer .eyJzdWIiOiJlbGVuYXN0b2lhbjAwQGdtYWlsLmNvbSIsImlhdCI6MTY5MDE4OTY0MiwiZXhwIjoxNjkwMTkxMDgyfQ.AyDyj2FXhSQvd3Gh4LHkdU1nxLRkJcU-xUUj3WUO4ew");

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertNotEquals(channel.getName(), newChannelName);
    }

}