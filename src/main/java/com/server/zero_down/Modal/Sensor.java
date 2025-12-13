package com.server.zero_down.Modal;

import com.server.zero_down.Common.Enums.SensorType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "tb_sensor")
public class Sensor extends BaseEntity {

    @ManyToOne(optional = true)
    @JoinColumn(name = "greenhouse_id", nullable = true)
    private Greenhouse greenhouse;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private SensorType type;

    @Column(length = 20)
    private String pinNumber;

    @Column(length = 20)
    private String location;

    @Column(length = 20)
    private String unit; // °C, %, lux, etc.

    @Column(nullable = false)
    private boolean active = true;

    @OneToMany(mappedBy = "sensor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SensorReading> readings = new ArrayList<>();

    @OneToMany(mappedBy = "sensor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Alert> alerts = new ArrayList<>();

    // Getters and setters

}

