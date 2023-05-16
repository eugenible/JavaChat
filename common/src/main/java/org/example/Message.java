package org.example;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class Message {

    private MessageType messageType;
    private String content;

    public Message(MessageType messageType, String content) {
        this.messageType = messageType;
        this.content = content;
    }

    public Message() {
    }
}
