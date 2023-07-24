package com.ai.gardening.service;

import com.ai.gardening.dtos.ChannelResponse;
import com.ai.gardening.dtos.CreateChannelRequest;
import com.ai.gardening.entity.AppUser;
import com.ai.gardening.entity.Channel;
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

import static org.junit.jupiter.api.Assertions.*;
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
    void testGetAllChannelsByUserId_Success() {
        AppUser appUser = new AppUser();
        appUser.setId(1L);
        when(appUserService.findCurrentAppUser(anyString())).thenReturn(appUser);

        List<Channel> channelList = new ArrayList<>();
        Channel channel1 = new Channel();
        Channel channel2 = new Channel();
        channelList.add(channel1);
        channelList.add(channel2);

        ResponseEntity<List<ChannelResponse>> response = channelService.getAllChannelsByUserId("token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
       // assertEquals(response.getBody().stream().toList(), channelList);
    }
}