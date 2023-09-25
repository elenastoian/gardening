package com.ai.gardening.dtos;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AllChannelsResponse {

    private Long id;
    private String channelsNames;
    private boolean isBlocked;
    private AppUserResponse owner;
    private List<AppUserResponse> joinedAppUsers = new ArrayList<>() ;
}
