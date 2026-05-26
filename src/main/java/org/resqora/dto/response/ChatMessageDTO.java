package org.resqora.dto.response;

import lombok.Data;

@Data
public class ChatMessageDTO {

    private Long senderId;
    private Long receiverId;
    private Long bookingId;
    private String content;
}
