package com.server.zero_down.Repository;

import com.server.zero_down.Common.Enums.SensorType;
import com.server.zero_down.Modal.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SensorRepository extends JpaRepository<Sensor, String> {

    List<Sensor> findByType(SensorType type);

    Sensor findFirstByType(SensorType type);
}

