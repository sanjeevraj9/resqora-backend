package org.resqora.service;

import org.resqora.dto.request.CreateReviewRequest;
import org.resqora.dto.response.ReviewResponse;

import java.util.List;

public interface ReviewService {

    void createReview(
            CreateReviewRequest request,
            String email
    );

    List<ReviewResponse> getMechanicReviews(
            String email
    );
}