package com.ai.gardening.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddPostRequest {
    @NotNull(message = "Post's title cannot be null")
    @NotBlank(message = "Post's title cannot be blank")
    @NotEmpty(message = "Post's title cannot be empty")
    @Size(min = 1, max = 255, message = "Post's title size has to be 1-255 characters")
    private String title;

    @NotNull(message = "Post's description cannot be null")
    @NotBlank(message = "Post's description cannot be blank")
    @NotEmpty(message = "Post's description cannot be empty")
    private String description;

    @NotNull(message = "Post's channel id cannot be null")
    private Long channelId;
}
