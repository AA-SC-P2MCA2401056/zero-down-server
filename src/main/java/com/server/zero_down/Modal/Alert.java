package com.server.zero_down.Modal;

import com.server.zero_down.Common.Enums.AlertLevel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "tb_alert")
public class Alert extends BaseEntity {

    @ManyToOne(optional = true)
    @JoinColumn(name = "greenhouse_id")
    private Greenhouse greenhouse;

    @ManyToOne
    @JoinColumn(name = "sensor_id")
    private Sensor sensor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AlertLevel level = AlertLevel.WARN;

    @Column(nullable = false, length = 255)
    private String message;

    @Column(nullable = false)
    private boolean resolved = false;

    private LocalDateTime resolvedAt;

}

