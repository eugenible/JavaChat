package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class JsonMessageConverter {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static String messageToJson(Message message) throws IOException {
        return OBJECT_MAPPER.writeValueAsString(message);
    }

    public static Message jsonToMessage(String jsonString) throws IOException {
        return OBJECT_MAPPER.readValue(jsonString, Message.class);
    }
}
