package org.resqora.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LiveLocationNotification {

    private Long requestId;
    private Double latitude;
    private Double longitude;
}
