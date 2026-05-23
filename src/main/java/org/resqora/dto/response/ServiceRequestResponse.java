package org.resqora.dto.response;

import lombok.Builder;
import lombok.Data;
import org.resqora.enums.IssueType;
import org.resqora.enums.PaymentMethod;
import org.resqora.enums.PaymentStatus;
import org.resqora.enums.RequestStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class ServiceRequestResponse {

    private Long id;
    private Long vehicleId;
    private IssueType issueType;
    private String description;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private RequestStatus status;
    private BigDecimal estimatedPrice;
    private LocalDateTime createdAt;
    private String vehicleBrand;
    private String vehicleModel;
    private String mechanicName;
    private String customerName;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
}
