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
public class UpdateChannelRequest {

    @NotNull(message = "Channel's id cannot be null")
    private Long channelId;

    @NotNull(message = "Channel's new name cannot be null")
    @NotBlank(message = "Channel's new name cannot be blank")
    @NotEmpty(message = "Channel's new name cannot be empty")
    @Size(min = 1, max = 100, message = "Channel's new name size has to be 1-100 characters")
    private String name;
}
