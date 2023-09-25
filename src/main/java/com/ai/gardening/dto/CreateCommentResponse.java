package com.ai.gardening.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class CreateCommentResponse {
    private String comment;
    private String ownerName;
    private Long postId;

}
