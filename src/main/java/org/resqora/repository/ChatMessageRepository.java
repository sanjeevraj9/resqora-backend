package org.resqora.repository;

import org.resqora.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage,Long> {

    List<ChatMessage> findByBookingIdOrderByTimeStampAsc(Long bookingId);

    List<ChatMessage> findBySenderIdAndReceiverIdOrReceiverIdAndSenderIdOrderByTimeStampAsc(
            Long senderId, Long receiverId, Long receiverId2, Long senderId2);
}
