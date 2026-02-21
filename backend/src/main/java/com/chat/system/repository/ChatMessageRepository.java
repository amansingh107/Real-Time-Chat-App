package com.chat.system.repository;

import com.chat.system.entity.ChatMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, Long> {
    List<ChatMessageEntity> findByRoomIdOrderByCreatedAtAsc(String roomId);
    List<ChatMessageEntity> findTop50ByRoomIdOrderByCreatedAtDesc(String roomId);
}
