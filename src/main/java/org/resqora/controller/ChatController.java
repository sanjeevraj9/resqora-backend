package org.resqora.controller;

import lombok.RequiredArgsConstructor;
import org.resqora.dto.response.ChatMessageDTO;
import org.resqora.entity.ChatMessage;
import org.resqora.service.impl.ChatServiceImpl;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ChatController {
    private final ChatServiceImpl chatService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.send")
    public void sendMessage(@Payload ChatMessageDTO dto) {
        ChatMessage saved = chatService.saveMessage(dto);

        messagingTemplate.convertAndSend(
                "/topic/chat/" + dto.getReceiverId(), saved
        );

        messagingTemplate.convertAndSend(
                "/topic/chat/" + dto.getSenderId(), saved
        );
    }

    @GetMapping("/api/chat/history/{bookingId}")
    @ResponseBody
    public List<ChatMessage> getChatHistory(@PathVariable Long bookingId) {
        return chatService.getChatHistory(bookingId);
    }
}
