package com.server.zero_down.Common.Utils;

import com.server.zero_down.Common.Enums.SensorType;

import java.util.Map;

public class ThresholdConfig {
    public static final Map<SensorType, Double> MIN = Map.of(
            SensorType.TEMPERATURE, 15.0,
            SensorType.HUMIDITY, 40.0,
            SensorType.SOIL_MOISTURE, 30.0,
            SensorType.LIGHT, 200.0
    );

    public static final Map<SensorType, Double> MAX = Map.of(
            SensorType.TEMPERATURE, 30.0
    );
}
