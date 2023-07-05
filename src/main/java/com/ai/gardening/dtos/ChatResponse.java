package com.ai.gardening.dtos;

import lombok.*;

import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class ChatResponse {

    private List<Choice> choices;


    public static class Choice {

        private int index;
        private MessageRequest message;

        public Choice(int index, MessageRequest message) {
            this.index = index;
            this.message = message;
        }

        public Choice() {
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public MessageRequest getMessage() {
            return message;
        }

        public void setMessage(MessageRequest message) {
            this.message = message;
        }
    }
}

