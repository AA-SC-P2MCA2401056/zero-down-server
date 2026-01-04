package com.server.zero_down.Dto.View;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SensorLogResponse {
    private String id;
    private Double value;
    private LocalDateTime recordedAt;
    private String recordedTime;
}

