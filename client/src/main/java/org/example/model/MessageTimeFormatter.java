package org.example.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

public class MessageTimeFormatter {
    public static String addTimeToMessage(String message) {
        LocalDateTime time = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        String strTime = time.format(DateTimeFormatter.ofPattern("'['HH:mm:ss']'", Locale.US));
        return strTime + " " + message;
    }
}
