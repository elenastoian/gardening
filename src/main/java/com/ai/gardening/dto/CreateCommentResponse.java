package com.ai.gardening.dto;

import com.ai.gardening.entity.AppUser;
import com.ai.gardening.entity.Post;
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
    private Post post;

}
