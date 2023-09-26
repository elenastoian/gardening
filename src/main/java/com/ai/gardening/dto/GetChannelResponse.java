package com.ai.gardening.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class GetChannelResponse {
    private Long id;
    private String channelName;
    private Long ownerId;
    private List<Long> joinedAppUsersIds = new ArrayList<>();
    private List<Long> postsIds = new ArrayList<>();
}
