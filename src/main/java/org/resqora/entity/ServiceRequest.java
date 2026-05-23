package org.resqora.entity;


import jakarta.persistence.*;
import lombok.*;
import org.resqora.enums.IssueType;
import org.resqora.enums.PaymentMethod;
import org.resqora.enums.PaymentStatus;
import org.resqora.enums.RequestStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="service_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name="user_id",nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name="vehicle_id",nullable = false)
    private Vehicle vehicle;

    @ManyToOne
    @JoinColumn(name="mechanic_id")
    private User mechanic;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IssueType issueType;

    @Column(length = 500)
    private String description;

    @Column(nullable = false,precision = 10,scale = 7)
    private BigDecimal latitude;

    @Column(nullable = false,precision = 10,scale = 7)
        private BigDecimal longitude;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status;
    private BigDecimal estimatedPrice;
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
    @ManyToMany
    @JoinTable(
            name = "request_rejections",
            joinColumns = @JoinColumn(name = "request_id"),
            inverseJoinColumns = @JoinColumn(name = "mechanic_id")
    )
    private List<User> rejectedMechanics = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @PrePersist
    public void prePersist(){
        createdAt=LocalDateTime.now();
        updatedAt=LocalDateTime.now();
        status=RequestStatus.REQUESTED;


    }

    @PreUpdate
    public void preUpdate(){
        updatedAt=LocalDateTime.now();
    }




}
