package org.resqora.repository;

import org.resqora.entity.ServiceRequest;
import org.resqora.entity.User;
import org.resqora.enums.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ServiceRequestRepository extends JpaRepository<ServiceRequest,Long> {

    List<ServiceRequest> findByUserOrderByCreatedAtDesc(User user);

    Optional<ServiceRequest> findByIdAndUser(Long id, User user);

    Optional<ServiceRequest> findByIdAndMechanic(Long id, User mechanic);

    List<ServiceRequest> findByMechanicAndStatusNotOrderByCreatedAtDesc(
            User mechanic,
            RequestStatus status
    );

    List<ServiceRequest> findByMechanicOrderByCreatedAtDesc(User mechanic);
    List<ServiceRequest> findByMechanic(User mechanic);

    List<ServiceRequest> findByMechanicAndStatusIn(
            User mechanic,
            List<RequestStatus> statuses
    );

}
