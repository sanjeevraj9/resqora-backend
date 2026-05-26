package org.resqora.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LiveLocationNotification {

    private Long requestId;
    private Double latitude;
    private Double longitude;
}
