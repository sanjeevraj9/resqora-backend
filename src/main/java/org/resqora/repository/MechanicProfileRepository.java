package org.resqora.repository;

import org.resqora.entity.MechanicProfile;
import org.resqora.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MechanicProfileRepository extends JpaRepository<MechanicProfile,Long> {

    Optional<MechanicProfile> findByUser(User user);

    List<MechanicProfile> findByAvailabilityTrue();
}
