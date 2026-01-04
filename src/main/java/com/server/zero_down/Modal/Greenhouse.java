package com.server.zero_down.Modal;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "tb_greenhouse")
public class Greenhouse extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 255)
    private String location;

    @Column(columnDefinition = "TEXT")
    private String description;

    @OneToMany(mappedBy = "greenhouse", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Sensor> sensors = new ArrayList<>();

    // Getters and setters

}

