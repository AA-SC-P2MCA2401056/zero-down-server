package com.server.zero_down.Dto.Forms;

import com.server.zero_down.Common.Enums.SensorType;
import lombok.Data;

@Data
public class SensorUpdateRequest {
    private String name;
    private String location;
    private String pinNumber;
    private String unit;
    private Boolean active;
    private String greenhouseId;
    private SensorType type;
}
