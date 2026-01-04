package com.server.zero_down.Controller;

import com.server.zero_down.Dto.Forms.SensorCreateRequest;
import com.server.zero_down.Dto.Forms.SensorUpdateRequest;
import com.server.zero_down.Dto.View.SensorResponse;
import com.server.zero_down.Service.AdminSensorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/sensors")
@RequiredArgsConstructor
public class AdminSensorController {

    private final AdminSensorService adminSensorService;

    // 🔐 (optional later) @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<SensorResponse> createSensor(@RequestBody SensorCreateRequest request) {
        SensorResponse response = adminSensorService.createSensor(request);
        return ResponseEntity.ok(response);
    }

    // 🔐 (optional later) @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<SensorResponse>> getAllSensors() {
        List<SensorResponse> sensors = adminSensorService.getAllSensors();
        return ResponseEntity.ok(sensors);
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<SensorResponse> updateSensor(
            @PathVariable String id,
            @RequestBody SensorUpdateRequest request
    ) throws ChangeSetPersister.NotFoundException {
        SensorResponse response = adminSensorService.updateSensor(id, request);
        return ResponseEntity.ok(response);
    }

    // get
    @GetMapping("/{id}")
    public ResponseEntity<SensorResponse> getSensorById(
            @PathVariable String id
    ) {
        SensorResponse response = adminSensorService.getMeta(id);
        return ResponseEntity.ok(response);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSensor(@PathVariable String id) {
        adminSensorService.deleteSensor(id);
        return ResponseEntity.ok().build();
    }
}

