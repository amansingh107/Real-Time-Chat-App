package com.chat.system.repository;

import com.chat.system.entity.RoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<RoomEntity, Long> {
    Optional<RoomEntity> findByRoomId(String roomId);
    boolean existsByRoomId(String roomId);
    List<RoomEntity> findByIsPrivateFalseOrderByCreatedAtDesc();
    List<RoomEntity> findByCreatedByOrderByCreatedAtDesc(String createdBy);
}
