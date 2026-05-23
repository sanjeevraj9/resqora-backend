package org.resqora.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RequestClosedNotification {

    private Long requestId;
    private String message;
}
