package org.resqora.dto.response;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NearbyMechanicResponse {

    private Long mechanicId;
    private String mechanicName;
    private String shopName;
    private Double rating;
    private Double distanceKm;
    private Double latitude;
    private Double longitude;

}
