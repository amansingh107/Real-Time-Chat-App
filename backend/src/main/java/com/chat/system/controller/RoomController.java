package com.chat.system.controller;

import com.chat.system.model.CreateRoomRequest;
import com.chat.system.model.RoomDTO;
import com.chat.system.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RoomController {
    
    private final RoomService roomService;
    
    /**
     * Create a new chat room
     * Expects username to be passed as a request parameter
     */
    @PostMapping("/create")
    public ResponseEntity<RoomDTO> createRoom(
            @RequestBody CreateRoomRequest request,
            @RequestParam String username) {
        log.info("Creating new room: name={}, createdBy={}, isPrivate={}", 
                request.getName(), username, request.getIsPrivate());
        
        RoomDTO room = roomService.createRoom(request, username);
        
        log.info("Room created successfully: roomId={}", room.getRoomId());
        return ResponseEntity.ok(room);
    }
    
    /**
     * Get all public rooms
     */
    @GetMapping
    public ResponseEntity<List<RoomDTO>> getAllPublicRooms() {
        log.info("Fetching all public rooms");
        List<RoomDTO> rooms = roomService.getAllPublicRooms();
        log.info("Found {} public rooms", rooms.size());
        return ResponseEntity.ok(rooms);
    }
    
    /**
     * Get room details by roomId
     */
    @GetMapping("/{roomId}")
    public ResponseEntity<RoomDTO> getRoomByRoomId(@PathVariable String roomId) {
        log.info("Fetching room details: roomId={}", roomId);
        
        RoomDTO room = roomService.getRoomByRoomId(roomId);
        if (room != null) {
            log.info("Room found: {}", room.getName());
            return ResponseEntity.ok(room);
        } else {
            log.warn("Room not found: roomId={}", roomId);
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Get all rooms created by a specific user
     */
    @GetMapping("/my-rooms/{username}")
    public ResponseEntity<List<RoomDTO>> getRoomsByUser(@PathVariable String username) {
        log.info("Fetching rooms created by user: {}", username);
        List<RoomDTO> rooms = roomService.getRoomsByUser(username);
        log.info("Found {} rooms created by {}", rooms.size(), username);
        return ResponseEntity.ok(rooms);
    }
}
