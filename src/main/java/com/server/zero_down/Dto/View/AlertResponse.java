package com.server.zero_down.Dto.View;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AlertResponse {

    private String id;

    private String type;        // AIR_HEAT, DRY_SOIL, etc

    private String message;

    private String sensorId;    // Which sensor caused this alert

    private String level;       // CRITICAL / WARN

    private boolean resolved;

    private LocalDateTime createdAt;

    private LocalDateTime resolvedAt;
}

