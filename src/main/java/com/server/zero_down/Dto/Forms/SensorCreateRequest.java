package com.server.zero_down.Dto.Forms;

import com.server.zero_down.Common.Enums.SensorType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SensorCreateRequest {

    private String name;          // e.g. "GH1 Temperature"
    private SensorType type;      // TEMPERATURE / HUMIDITY / SOIL_MOISTURE / LIGHT
    private String location;      // e.g. "Greenhouse 1"
}

