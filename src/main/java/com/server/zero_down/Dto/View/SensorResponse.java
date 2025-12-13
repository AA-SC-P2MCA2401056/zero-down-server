package com.server.zero_down.Dto.View;

import com.server.zero_down.Common.Enums.SensorType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SensorResponse {

    private String id;            // 12-char ID from BaseEntity
    private String name;
    private SensorType type;
    private String location;
}

