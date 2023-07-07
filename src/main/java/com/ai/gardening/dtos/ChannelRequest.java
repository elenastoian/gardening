package com.ai.gardening.dtos;

import com.ai.gardening.entity.AppUser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ChannelRequest {
    private String name;
    private long creatorId;
}
