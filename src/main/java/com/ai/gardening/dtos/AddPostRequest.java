package com.ai.gardening.dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddPostRequest {
    private String title;
    private String description;
    private Long channelId;
}
