package com.ai.gardening.service;

import com.ai.gardening.entity.AppUser;
import com.ai.gardening.entity.Channel;
import com.ai.gardening.entity.Token;
import com.ai.gardening.repository.AppUserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppUserServiceTest {

    @InjectMocks
    AppUserService appUserService;

    @Mock
    private  AppUserRepository appUserRepository;

    @Test
    void removeJoinedAppUserFromChannel() {
        AppUser appUser = new AppUser();
        List<AppUser> joinedAppUsers = new ArrayList<>();
        joinedAppUsers.add(appUser);

        Channel channel = new Channel();
        List<Channel> joinedChannels = new ArrayList<>();
        joinedChannels.add(channel);

        appUser.setJoinedChannels(joinedChannels);
        channel.setJoinedAppUsers(joinedAppUsers);

        Assertions.assertEquals(appUser.getJoinedChannels(),joinedChannels);
        Assertions.assertEquals(channel.getJoinedAppUsers(),joinedAppUsers);

        appUserService.removeJoinedAppUserFromChannel(appUser, channel);

        Assertions.assertFalse(appUser.getJoinedChannels().contains(joinedChannels));
        Assertions.assertFalse(channel.getJoinedAppUsers().contains(joinedAppUsers));
    }
}