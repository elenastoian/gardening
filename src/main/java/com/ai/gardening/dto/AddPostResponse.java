package com.ai.gardening.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class AddPostResponse {
    private String title;
    private String description;
    private String userName;
    private String channelName;
}
