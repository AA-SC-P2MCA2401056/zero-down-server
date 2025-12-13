package com.server.zero_down.Dto.Forms;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class SensorReadingRequest {
    private String sensorId;
    private Double value;
    private LocalDateTime recordedAt;
}

