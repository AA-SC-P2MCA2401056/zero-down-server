package com.server.zero_down.Common.Utils;

import com.server.zero_down.Common.Enums.SensorType;

import java.util.Map;

public class ThresholdConfig {
    public static final Map<SensorType, Double> MIN = Map.of(
            SensorType.TEMPERATURE,   18.0,   // °C
            SensorType.HUMIDITY,      55.0,   // %
            SensorType.SOIL_MOISTURE, 35.0,   // %
            SensorType.LIGHT,         300.0   // LUX
    );

    public static final Map<SensorType, Double> MAX = Map.of(
            SensorType.TEMPERATURE,   30.0,   // °C
            SensorType.HUMIDITY,      80.0,   // %
            SensorType.SOIL_MOISTURE, 85.0,   // %
            SensorType.LIGHT,         1200.0  // LUX
    );
}
