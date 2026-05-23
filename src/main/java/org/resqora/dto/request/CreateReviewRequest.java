package org.resqora.dto.request;


import lombok.Data;

@Data
public class CreateReviewRequest {
    private Long requestId;
    private Integer rating;
    private String comment;
}
