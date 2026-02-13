package com.server.zero_down.Dto.View;

import com.server.zero_down.Common.Enums.SensorType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SensorResponse {

    private String id;            // 12-char ID from BaseEntity
    private String name;
    private SensorType type;
    private String location;
    private Boolean isActive;
    private LocalDateTime updatedAt;
    private LocalDateTime lastLiveAt;
    private LocalDateTime createdAt;
    private Boolean online;
}

