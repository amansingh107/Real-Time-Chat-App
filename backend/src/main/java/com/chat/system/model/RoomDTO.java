package com.chat.system.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomDTO {
    private String roomId;
    private String name;
    private String description;
    private String createdBy;
    private LocalDateTime createdAt;
    private Boolean isPrivate;
}
