package com.server.zero_down.Repository;

import com.server.zero_down.Common.Enums.SensorType;
import com.server.zero_down.Modal.SensorReading;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SensorReadingRepository extends JpaRepository<SensorReading, String> {

    Optional<SensorReading> findTopBySensorIdOrderByRecordedAtDesc(String sensorId);

    List<SensorReading> findBySensorIdAndRecordedAtBetweenOrderByRecordedAt(
            String sensorId,
            LocalDateTime from,
            LocalDateTime to
    );

    List<SensorReading> findBySensorId(String id);

    Optional<SensorReading> findFirstBySensor_TypeAndSensor_ActiveOrderByRecordedAtDesc(SensorType sensorType, boolean b);
}

