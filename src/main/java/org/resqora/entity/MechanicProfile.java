package org.resqora.entity;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name="mechanic_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MechanicProfile {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @OneToOne
    @JoinColumn(name="user_id",nullable = false,unique = true)
    private User user;

    @Column(nullable = false)
    private String shopName;
    @Column(nullable = false,precision=10,scale=7)
    private BigDecimal latitude;
    @Column(nullable = false,precision = 10,scale = 7)
    private BigDecimal longitude;
    @Column(nullable = false)
    private Boolean availability;
    @Column(precision=2,scale=1)
    private BigDecimal rating;
    private Integer experienceYears;
    private LocalDateTime createdAt;
    private String specialization;

    @PrePersist
    public void prePersist(){
        createdAt=LocalDateTime.now();
        availability=true;
        rating=BigDecimal.ZERO;
    }
}
