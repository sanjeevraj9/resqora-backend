package org.resqora.dto.request;

import lombok.Data;

@Data
public class OrderRequest {
    private Long serviceRequestId;
    private Long userId;
    private Double amount;
    private String phone;
    private String email;
}
