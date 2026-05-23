package org.resqora.repository;

import org.resqora.entity.User;
import org.resqora.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle,Long> {

    List<Vehicle> findByUser(User user);
    Optional<Vehicle> findByIdAndUser(Long id, User user);

    boolean existsByRegistrationNumber(String registrationNumber);
}
