package org.resqora.dto.response;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RequestStatusNotification {
    private Long requestId;
    private String status;
    private String message;
}
