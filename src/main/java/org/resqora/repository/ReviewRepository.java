package org.resqora.repository;

import org.resqora.entity.Review;
import org.resqora.entity.ServiceRequest;
import org.resqora.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review,Long> {

List<Review> findByMechanic(User mechanic);
    boolean existsByRequest(ServiceRequest request);
}
