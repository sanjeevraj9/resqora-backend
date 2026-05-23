package org.resqora.dto.response;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RequestNotificationResponse {

    private Long requestId;
    private String issueType;
    private String description;
    private Double latitude;
    private Double longitude;
    private Double estimatedPrice;

    private String customerName;
    private String customerPhone;
}
