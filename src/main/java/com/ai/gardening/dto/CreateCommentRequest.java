package com.ai.gardening.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreateCommentRequest {

    @NotNull(message = "Comment cannot be null")
    @NotBlank(message = "comment cannot be blank")
    @NotEmpty(message = "Comment cannot be empty")
    private String comment;

    @NotNull(message = "Comment's post cannot be null")
    private Long postId;
}
