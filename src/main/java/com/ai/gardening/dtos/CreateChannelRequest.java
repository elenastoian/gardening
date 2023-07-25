package com.ai.gardening.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateChannelRequest {

    @NotNull(message = "Channel's name cannot be null")
    @NotBlank(message = "Channel's name cannot be blank")
    @NotEmpty(message = "Channel's name cannot be empty")
    @Size(min = 1, max = 100, message = "Channel's name size has to be 1-100 characters")
    private String name;
}
