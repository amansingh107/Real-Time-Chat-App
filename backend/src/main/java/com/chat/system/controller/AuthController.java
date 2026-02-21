package com.chat.system.controller;

import com.chat.system.model.ChatMessage;
import com.chat.system.model.LoginRequest;
import com.chat.system.model.LoginResponse;
import com.chat.system.service.AuthService;
import com.chat.system.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // Allow CORS for frontend
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    private final ChatService chatService;
    
    @PostMapping("/auth/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        boolean authenticated = authService.authenticate(request.getUsername(), request.getPassword());
        
        if (authenticated) {
            return ResponseEntity.ok(new LoginResponse(
                true, 
                "Login successful", 
                request.getUsername()
            ));
        } else {
            return ResponseEntity.ok(new LoginResponse(
                false, 
                "Invalid username or password", 
                null
            ));
        }
    }
    
    @PostMapping("/auth/register")
    public ResponseEntity<LoginResponse> register(@RequestBody LoginRequest request) {
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            return ResponseEntity.ok(new LoginResponse(
                false, 
                "Username cannot be empty", 
                null
            ));
        }
        
        if (request.getPassword() == null || request.getPassword().length() < 6) {
            return ResponseEntity.ok(new LoginResponse(
                false, 
                "Password must be at least 6 characters", 
                null
            ));
        }
        
        boolean registered = authService.registerUser(request.getUsername(), request.getPassword());
        
        if (registered) {
            return ResponseEntity.ok(new LoginResponse(
                true, 
                "Registration successful", 
                request.getUsername()
            ));
        } else {
            return ResponseEntity.ok(new LoginResponse(
                false, 
                "Username already exists", 
                null
            ));
        }
    }
    
    @GetMapping("/chat/history/{roomId}")
    public ResponseEntity<List<ChatMessage>> getChatHistory(
            @PathVariable String roomId,
            @RequestParam(defaultValue = "50") int limit) {
        List<ChatMessage> history = chatService.getChatHistory(roomId, limit);
        return ResponseEntity.ok(history);
    }
}
