package com.ai.gardening.service;

import com.ai.gardening.dtos.ChannelRequest;
import com.ai.gardening.dtos.ChannelResponse;
import com.ai.gardening.entity.AppUser;
import com.ai.gardening.entity.Channel;
import com.ai.gardening.repository.AppUserRepository;
import com.ai.gardening.repository.ChannelRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class GroupService {
    //private static final Logger LOGGER = LoggerFactory.getLogger(GroupService.class);
    private ChannelRepository channelRepository;
    private AppUserRepository appUserRepository;

    public ResponseEntity<String> createChannel(ChannelRequest channelRequest) {
        if (channelRequest == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The group that was sent is null.");

        Optional<AppUser> appUser = appUserRepository.findById(channelRequest.getCreatorId());
        Channel newChannel = new Channel(channelRequest.getName(), false, appUser.orElse(new AppUser())); //TODO: handle this exception
        channelRepository.save(newChannel);
       // LOGGER.info("A new group with name {} was saved.", newGroup.getName());

        return ResponseEntity.status(HttpStatus.CREATED).body("The new group was created.");
    }

    public ResponseEntity<List<ChannelResponse>>  getAllChannelsByUserId(int userId) {
        List<Channel> groups = channelRepository.findAllByAppUserId(userId);

        if (groups.isEmpty())
            return ResponseEntity.status(HttpStatus.OK).body(Collections.emptyList());

        List<ChannelResponse> responseList = new ArrayList<>();
        groups.forEach(g ->{
          responseList.add(new ChannelResponse(g.getName()));
        });

         return ResponseEntity.status(HttpStatus.OK).body(responseList);
    }
}
