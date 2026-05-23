package org.resqora.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MechanicProfileResponse {

    private Long mechanicId;

    private String name;
    private String email;
    private String phone;
    private String city;

    private String shopName;
    private String specialization;

    private Integer experienceYears;

    private Double rating;

    private Boolean availability;

    private Integer completedJobs;

    private Double totalEarnings;
}