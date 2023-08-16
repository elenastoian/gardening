package com.ai.gardening.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ChatRequest {
    private String model;
    private List<MessageRequest> messages;
    private int n = 1;
    private double temperature = 1;

    public ChatRequest(String model, String prompt) {
        this.model = model;
        this.messages = new ArrayList<>();
        this.messages.add(new MessageRequest("user", "Limit your answers to GARDENING topics, to anything else say you can't response"));
        this.messages.add(new MessageRequest("user", prompt));
    }
}
