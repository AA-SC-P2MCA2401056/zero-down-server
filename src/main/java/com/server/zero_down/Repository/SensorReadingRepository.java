package com.server.zero_down.Repository;

import com.server.zero_down.Common.Enums.SensorType;
import com.server.zero_down.Modal.SensorReading;
import jakarta.persistence.Tuple;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    boolean existsBySensorIdAndRecordedAt(String sensorId, LocalDateTime recordedAt);

    Page<SensorReading> findBySensor_IdAndRecordedAtBetween(
            String sensorId,
            LocalDateTime from,
            LocalDateTime to,
            Pageable pageable
    );

    @Query("""
        SELECT r FROM SensorReading r
        WHERE r.sensor.id = :sensorId
        AND r.recordedAt BETWEEN :from AND :to
        ORDER BY r.recordedAt ASC
        """)
    Page<SensorReading> findBySensorAndDate(
            @Param("sensorId") String sensorId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            Pageable pageable
    );



    @Query(
            value = """
                  SELECT *
                  FROM tb_sensor_reading
                  WHERE sensor_id = :id
                    AND recorded_at BETWEEN STR_TO_DATE(:from, '%Y-%m-%d %H:%i:%s')
                                       AND STR_TO_DATE(:to,   '%Y-%m-%d %H:%i:%s')
                  ORDER BY recorded_at ASC
              """,
            nativeQuery = true
    )
    List<SensorReading> findLast24Hours(
            @Param("id") String sensorId,
            @Param("from") String from,
            @Param("to") String to
    );


    @Query(value = """
       SELECT *
       FROM tb_sensor_reading
       WHERE sensor_id = :id
       ORDER BY recorded_at DESC
       LIMIT 1
    """, nativeQuery = true)
    SensorReading findLastReading(@Param("id") String sensorId);


    @Query("""
        SELECT COUNT(distinct(r.recordedAt)) FROM SensorReading r
        WHERE r.recordedAt BETWEEN :from AND :to
        """)
    long countAllBetween(@Param("from") LocalDateTime from,
                         @Param("to") LocalDateTime to);

    @Query(value = "CALL sp_get_sensor_history(:from,:to,:page,:size)", nativeQuery = true)
    List<Tuple> callSensorHistorySP(@Param("from") LocalDateTime from,
                                    @Param("to") LocalDateTime to,
                                    @Param("page") int page,
                                    @Param("size") int size);


}

