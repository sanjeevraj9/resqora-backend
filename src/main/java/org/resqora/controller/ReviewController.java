package org.resqora.controller;

import lombok.RequiredArgsConstructor;
import org.resqora.dto.request.CreateReviewRequest;
import org.resqora.dto.response.ReviewResponse;
import org.resqora.service.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<String> createReview(
            @RequestBody CreateReviewRequest request,
            Authentication authentication
    ) {
        System.out.println("REQUEST ID: " + request.getRequestId());
        System.out.println("RATING: " + request.getRating());
        System.out.println("USER: " + authentication.getName());
        reviewService.createReview(
                request,
                authentication.getName()
        );

        return ResponseEntity.ok(
                "Review submitted successfully"
        );
    }

    @GetMapping("/mechanic")
    public ResponseEntity<List<ReviewResponse>>
    getMechanicReviews(
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                reviewService.getMechanicReviews(
                        authentication.getName()
                )
        );
    }
}