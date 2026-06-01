package org.resqora.controller;

import lombok.RequiredArgsConstructor;
import org.resqora.entity.ServiceRequest;
import org.resqora.enums.Role;
import org.resqora.repository.MechanicProfileRepository;
import org.resqora.repository.ServiceRequestRepository;
import org.resqora.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserRepository userRepository;
    private final ServiceRequestRepository serviceRequestRepository;
    private final MechanicProfileRepository mechnaicProfileRepository;

    @GetMapping("stats")
    public ResponseEntity<Map<String, Object>> getStatus() {
        Map<String, Object> stats = new HashMap<>();

        long totalUsers = userRepository.findAll()
                .stream()
                .filter(u -> u.getRole() == Role.USER)
                .count();

        long totalMechanics = userRepository.findAll()
                .stream()
                .filter(u -> u.getRole() == Role.MECHANIC)
                .count();

        List<ServiceRequest> allRequests =
                serviceRequestRepository.findAll();

        long totalBookings = allRequests.size();

        BigDecimal totalRevenue = allRequests.stream()
                .filter(r -> r.getEstimatedPrice() != null)
                .map(ServiceRequest::getEstimatedPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        stats.put("totalUsers", totalUsers);
        stats.put("totalMechanics", totalMechanics);
        stats.put("totalBookings", totalBookings);
        stats.put("totalRevenue", totalRevenue);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/users")
    public ResponseEntity<List<Map<String, Object>>> getAllUsers() {

        List<Map<String, Object>> users = userRepository.findAll()
                .stream()
                .filter(u -> u.getRole() == Role.USER)
                .map(u -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", u.getId());
                    map.put("name", u.getName());
                    map.put("email", u.getEmail());
                    map.put("phone", u.getPhone());
                    map.put("verified", u.getEmailVerified());
                    map.put("active", u.getIsActive());
                    return map;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @GetMapping("mechanics")
    public ResponseEntity<List<Map<String, Object>>> getAllMechanics() {

        List<Map<String, Object>> mechanics = userRepository.findAll()
                .stream()
                .filter(u -> u.getRole() == Role.MECHANIC)
                .map(u -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", u.getId());
                    map.put("name", u.getName());
                    map.put("email", u.getEmail());
                    map.put("phone", u.getPhone());

                    mechnaicProfileRepository.findByUser(u).ifPresent(p -> {
                        map.put("shopName", p.getShopName());
                        map.put("rating", p.getRating());
                        map.put("availability", p.getAvailability());
                    });
                    return map;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(mechanics);
    }

    @GetMapping("/bookings")
    public ResponseEntity<List<Map<String, Object>>> getAllBookings() {
        List<Map<String, Object>> bookings = serviceRequestRepository.findAll()
                .stream()
                .map(r -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", r.getId());
                    map.put("customerName", r.getUser().getName());
                    map.put("issueType", r.getIssueType());
                    map.put("status", r.getStatus());
                    map.put("paymentMethod", r.getPaymentMethod());
                    map.put("paymentStatus", r.getPaymentStatus());
                    map.put("estimatedPrice", r.getEstimatedPrice());
                    map.put("createdAt", r.getCreatedAt());

                    if (r.getMechanic() != null) {
                        map.put("mechanicName", r.getMechanic().getName());
                    }

                    return map;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(bookings);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        return ResponseEntity.ok("User deleted");
    }


    @DeleteMapping("/mechanics/{id}")
    public ResponseEntity<?> deleteMechanic(@PathVariable Long id) {
        userRepository.deleteById(id);
        return ResponseEntity.ok("Mechanic deleted");
    }
}

