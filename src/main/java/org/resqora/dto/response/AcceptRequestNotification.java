package org.resqora.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AcceptRequestNotification {
    private Long requestId;
    private String mechanicName;
    private String message;
}
