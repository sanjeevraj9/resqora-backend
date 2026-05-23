package org.resqora.service.impl;


import lombok.RequiredArgsConstructor;
import org.resqora.dto.request.CreateReviewRequest;
import org.resqora.dto.response.ReviewResponse;
import org.resqora.entity.MechanicProfile;
import org.resqora.entity.Review;
import org.resqora.entity.ServiceRequest;
import org.resqora.entity.User;
import org.resqora.enums.RequestStatus;
import org.resqora.exception.ResourceNotFoundException;
import org.resqora.repository.MechanicProfileRepository;
import org.resqora.repository.ReviewRepository;
import org.resqora.repository.ServiceRequestRepository;
import org.resqora.repository.UserRepository;
import org.resqora.service.ReviewService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl  implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ServiceRequestRepository serviceRequestRepository;
    private final MechanicProfileRepository mechanicProfileRepository;

    @Override
    public void createReview(
            CreateReviewRequest request,
            String email
    ) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        ));

        ServiceRequest serviceRequest =
                serviceRequestRepository.findById(
                        request.getRequestId()
                ).orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Request not found"
                        ));

        if (serviceRequest.getUser() == null) {
            throw new RuntimeException(
                    "Request user missing"
            );
        }

        if (!serviceRequest.getUser()
                .getId()
                .equals(user.getId())) {
            throw new RuntimeException(
                    "Unauthorized review"
            );
        }

        if (reviewRepository.existsByRequest(
                serviceRequest
        )) {
            throw new RuntimeException(
                    "Review already submitted"
            );
        }

        if (serviceRequest.getMechanic() == null) {
            throw new RuntimeException(
                    "No mechanic assigned"
            );
        }

        if (serviceRequest.getStatus() !=
                RequestStatus.COMPLETED) {
            throw new RuntimeException(
                    "Review only after completion"
            );
        }

        User mechanic =
                serviceRequest.getMechanic();

        Review review = Review.builder()
                .rating(request.getRating())
                .comment(request.getComment())
                .user(user)
                .mechanic(mechanic)
                .request(serviceRequest)
                .build();

        reviewRepository.save(review);

        MechanicProfile profile =
                mechanicProfileRepository
                        .findByUser(mechanic)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Mechanic profile not found"
                                ));

        List<Review> reviews =
                reviewRepository.findByMechanic(
                        mechanic
                );

        double avg = reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);

        profile.setRating(
                BigDecimal.valueOf(avg)
        );
        if (request.getRating() == null ||
                request.getRating() < 1 ||
                request.getRating() > 5) {
            throw new RuntimeException(
                    "Rating must be between 1 and 5"
            );
        }

        mechanicProfileRepository.save(profile);
    }
    @Override
    public List<ReviewResponse> getMechanicReviews(String email) {

        User mechanic = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Mechanic not found"));

        List<Review> reviews =
                reviewRepository.findByMechanic(mechanic);

        return reviews.stream()
                .map(review -> ReviewResponse.builder()
                        .rating(review.getRating())
                        .comment(review.getComment())
                        .userName(review.getUser().getName())
                        .build())
                .toList();
    }
}
