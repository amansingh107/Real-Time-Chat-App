package com.chat.system.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    public enum Type {
        CHAT, TYPING, JOIN, LEAVE
    }

    private Type type;       // CHAT | TYPING | JOIN | LEAVE
    private String content;  // The text (or empty for typing)
    private String sender;
    private String roomId;
}