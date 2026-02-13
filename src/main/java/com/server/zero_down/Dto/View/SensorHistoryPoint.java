package com.server.zero_down.Dto.View;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class SensorHistoryPoint {
    private LocalDate date;
    private long time;       // epoch millis
    private Double temp;     // TEMPERATURE
    private Double humidity; // HUMIDITY
    private Double soil;     // SOIL_MOISTURE
    private Double light;    // LIGHT
}
