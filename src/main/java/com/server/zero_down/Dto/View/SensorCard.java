package com.server.zero_down.Dto.View;

import com.server.zero_down.Common.Enums.SensorType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class SensorCard {

    private String sensorId;
    private String name;
    private SensorType type;
    private Double value;
    private LocalDateTime recordedAt;
}

