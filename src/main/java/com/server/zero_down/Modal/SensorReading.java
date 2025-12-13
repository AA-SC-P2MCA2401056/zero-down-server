package com.server.zero_down.Modal;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "tb_sensor_reading", indexes = {
        @Index(name = "idx_sensor_time", columnList = "sensor_id, recordedAt")
})
public class SensorReading extends BaseEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "sensor_id")
    private Sensor sensor;

    @Column(nullable = false)
    private LocalDateTime recordedAt;

    @Column(nullable = false)
    private Double value;
}

