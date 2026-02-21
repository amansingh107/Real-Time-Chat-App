package com.chat.system.service;

import com.chat.system.entity.RoomEntity;
import com.chat.system.model.CreateRoomRequest;
import com.chat.system.model.RoomDTO;
import com.chat.system.repository.RoomRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomService {
    
    private final RoomRepository roomRepository;
    
    @PostConstruct
    public void initDefaultRoom() {
        // Create default "general" room if it doesn't exist
        if (!roomRepository.existsByRoomId("general")) {
            RoomEntity generalRoom = new RoomEntity();
            generalRoom.setRoomId("general");
            generalRoom.setName("General");
            generalRoom.setDescription("Default public chat room for everyone");
            generalRoom.setCreatedBy("system");
            generalRoom.setIsPrivate(false);
            roomRepository.save(generalRoom);
            log.info("Created default 'general' room");
        }
    }
    
    public RoomDTO createRoom(CreateRoomRequest request, String createdBy) {
        // Generate unique room ID
        String roomId = generateRoomId();
        
        RoomEntity room = new RoomEntity();
        room.setRoomId(roomId);
        room.setName(request.getName());
        room.setDescription(request.getDescription());
        room.setCreatedBy(createdBy);
        room.setIsPrivate(request.getIsPrivate() != null ? request.getIsPrivate() : false);
        
        RoomEntity saved = roomRepository.save(room);
        log.info("Created new room: {} by user: {}", roomId, createdBy);
        
        return convertToDTO(saved);
    }
    
    public RoomDTO getRoomByRoomId(String roomId) {
        return roomRepository.findByRoomId(roomId)
            .map(this::convertToDTO)
            .orElse(null);
    }
    
    public List<RoomDTO> getAllPublicRooms() {
        return roomRepository.findByIsPrivateFalseOrderByCreatedAtDesc()
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    public List<RoomDTO> getRoomsByUser(String username) {
        return roomRepository.findByCreatedByOrderByCreatedAtDesc(username)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    public boolean roomExists(String roomId) {
        return roomRepository.existsByRoomId(roomId);
    }
    
    private String generateRoomId() {
        // Generate a short, readable room ID
        String uuid = UUID.randomUUID().toString();
        return uuid.substring(0, 8);
    }
    
    private RoomDTO convertToDTO(RoomEntity entity) {
        return new RoomDTO(
            entity.getRoomId(),
            entity.getName(),
            entity.getDescription(),
            entity.getCreatedBy(),
            entity.getCreatedAt(),
            entity.getIsPrivate()
        );
    }
}
