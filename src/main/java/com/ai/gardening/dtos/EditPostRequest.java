package com.ai.gardening.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EditPostRequest {
    private String title;
    private String description;
    private long appUserId;
    private long postId;
}
