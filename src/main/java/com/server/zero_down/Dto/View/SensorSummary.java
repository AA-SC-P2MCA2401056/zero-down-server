package com.server.zero_down.Dto.View;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SensorSummary {
    private double min;
    private double max;
    private double avg;
    private long count;
    private boolean stable;

    // NEW
    private LocalDateTime minTime;
    private LocalDateTime maxTime;
    private Long durationMinutes;
}

