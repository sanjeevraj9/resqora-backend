package org.resqora.service.impl;


import lombok.RequiredArgsConstructor;
import org.resqora.dto.response.ChatMessageDTO;
import org.resqora.entity.ChatMessage;
import org.resqora.repository.ChatMessageRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl {
    private final ChatMessageRepository chatMessageRepository;

    public ChatMessage saveMessage(ChatMessageDTO dto){
        ChatMessage message=new ChatMessage();
        message.setSenderId(dto.getSenderId());
        message.setReceiverId(dto.getReceiverId());
        message.setBookingId(dto.getBookingId());
        message.setContent(dto.getContent());
        return chatMessageRepository.save(message);
    }

    public List<ChatMessage> getChatHistory(Long bookingId){
        return chatMessageRepository.findByBookingIdOrderByTimeStampAsc(bookingId);
    }
}



