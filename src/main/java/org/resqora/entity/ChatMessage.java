package org.resqora.entity;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name="chat_message")
@Data
@NoArgsConstructor
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long senderId;
    private Long receiverId;
    private Long bookingId;
    @Column(columnDefinition = "TEXT")
    private String content;
    private LocalDateTime timeStamp;

    @Enumerated(EnumType.STRING)
    private MessageStatus status;
    @PrePersist
    public void prePersist(){
        this.timeStamp=LocalDateTime.now();
        this.status=MessageStatus.SENT;
    }

    public enum MessageStatus{
        SENT,DELIVERED,READ
    }
}
