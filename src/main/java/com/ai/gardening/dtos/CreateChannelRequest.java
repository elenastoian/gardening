package com.ai.gardening.dtos;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateChannelRequest {

    @NonNull
    private String name;
}
