package org.resqora.dto.request;

import lombok.Data;

@Data
public class PaymentUpdateRequest {
    private String paymentMethod;
    private String paymentStatus;
}
