package com.ai.gardening.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePostRequest {

    @NotNull(message = "Post's id cannot be null")
    private Long postId;

    @NotNull(message = "Post's new title cannot be null")
    @NotBlank(message = "Post's new title cannot be blank")
    @NotEmpty(message = "Post's new title cannot be empty")
    @Size(min = 1, max = 255, message = "Post's new title size has to be 1-255 characters")
    private String title;

    @NotNull(message = "Post's description cannot be null")
    @NotBlank(message = "Post's description cannot be blank")
    @NotEmpty(message = "Post's description cannot be empty")
    private String description;
}
