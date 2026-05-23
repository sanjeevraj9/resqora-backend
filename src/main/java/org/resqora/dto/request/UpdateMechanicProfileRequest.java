package org.resqora.dto.request;

import lombok.Data;

@Data
public class UpdateMechanicProfileRequest {

    private String name;
    private String phone;
    private String city;
    private String specialization;
    private Integer experienceYears;
}