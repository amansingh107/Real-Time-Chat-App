package com.chat.system.service;

import com.chat.system.entity.UserEntity;
import com.chat.system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    @PostConstruct
    public void initDemoUsers() {
        // Create demo users if they don't exist
        if (!userRepository.existsByUsername("alice")) {
            UserEntity alice = new UserEntity();
            alice.setUsername("alice");
            alice.setPassword(passwordEncoder.encode("password123"));
            userRepository.save(alice);
        }
        
        if (!userRepository.existsByUsername("bob")) {
            UserEntity bob = new UserEntity();
            bob.setUsername("bob");
            bob.setPassword(passwordEncoder.encode("password123"));
            userRepository.save(bob);
        }
        
        if (!userRepository.existsByUsername("charlie")) {
            UserEntity charlie = new UserEntity();
            charlie.setUsername("charlie");
            charlie.setPassword(passwordEncoder.encode("password123"));
            userRepository.save(charlie);
        }
    }
    
    public boolean authenticate(String username, String password) {
        return userRepository.findByUsername(username)
            .map(user -> passwordEncoder.matches(password, user.getPassword()))
            .orElse(false);
    }
    
    public boolean registerUser(String username, String password) {
        if (userRepository.existsByUsername(username)) {
            return false; // User already exists
        }
        
        UserEntity newUser = new UserEntity();
        newUser.setUsername(username);
        newUser.setPassword(passwordEncoder.encode(password));
        userRepository.save(newUser);
        return true;
    }
    
    public boolean userExists(String username) {
        return userRepository.existsByUsername(username);
    }
}
