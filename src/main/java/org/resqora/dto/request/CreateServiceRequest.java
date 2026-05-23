package org.resqora.dto.request;


import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.resqora.enums.IssueType;
import org.resqora.enums.PaymentMethod;
import org.resqora.enums.PaymentStatus;

import java.math.BigDecimal;

@Data
public class CreateServiceRequest {

    @NotNull
    private Long vehicleId;

    @NotNull
    private IssueType issueType;
    @NotNull
    private String description;

    @NotNull
    private BigDecimal latitude;
    @NotNull
    private BigDecimal longitude;

    private BigDecimal estimatedPrice;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
}
