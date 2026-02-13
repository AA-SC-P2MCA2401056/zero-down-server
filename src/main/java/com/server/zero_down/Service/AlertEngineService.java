package com.server.zero_down.Service;

import com.server.zero_down.Common.Enums.AlertLevel;
import com.server.zero_down.Common.Enums.SensorType;
import com.server.zero_down.Common.Utils.ThresholdConfig;
import com.server.zero_down.Dto.View.AlertResponse;
import com.server.zero_down.Dto.Forms.SensorReadingRequest;
import com.server.zero_down.Dto.View.SuccessResponse;
import com.server.zero_down.Modal.Alert;
import com.server.zero_down.Modal.Sensor;
import com.server.zero_down.Repository.AlertRepository;
import com.server.zero_down.Repository.SensorRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AlertEngineService {
    private final SensorRepository sensorRepo;
    private final AlertRepository alertRepo;
    private final SensorReadingService processor;

    public void evaluate(SensorReadingRequest r) {

        // 1️⃣ Load Sensor entity using sensorId from request
        Sensor sensor = sensorRepo.findById(r.getSensorId())
                .orElseThrow(() -> new RuntimeException("Sensor not found"));

        // 2️⃣ Identify what type of sensor this is (TEMP / HUM / SOIL / LIGHT)
        SensorType type = sensor.getType();

        // 3️⃣ Actual sensor reading value
        Double value = r.getValue();

        // 4️⃣ Check MAX thresholds (example: temperature > 35°C)
        if (ThresholdConfig.MAX.containsKey(type) &&
                value > ThresholdConfig.MAX.get(type)) {

            create(sensor, "High " + type + " detected", AlertLevel.CRITICAL, r);
        }

        // 5️⃣ Check MIN thresholds (example: soil < 30%)
        if (ThresholdConfig.MIN.containsKey(type) &&
                value < ThresholdConfig.MIN.get(type)) {

            create(sensor, "Low " + type + " detected", AlertLevel.WARN, r);
        }
    }

    // 6️⃣ Save alert only if same unresolved alert does not already exist
    private void create(Sensor sensor, String msg, AlertLevel level, SensorReadingRequest r) {

        Alert active = alertRepo.findFirstBySensorAndMessageAndResolvedFalseOrderByCreatedAtDesc(sensor, msg);

        // No active alert → create parent
        if (active == null) {
            saveNew(sensor, msg, level, null);
            processor.saveReading(r);
            return;
        }

        // Check if 30 minutes passed
        if (active.getCreatedAt().plusMinutes(10).isBefore(LocalDateTime.now())) {
            saveNew(sensor, msg, level, active.getParent() == null ? active : active.getParent());
            processor.saveReading(r);
        }
    }

    private void saveNew(Sensor sensor, String msg, AlertLevel level, Alert parent) {
        Alert a = new Alert();
        a.setSensor(sensor);
        a.setMessage(msg);
        a.setLevel(level);
        a.setParent(parent);
        alertRepo.save(a);
    }


    public List<AlertResponse> findAll(Sort createdAt) {
        List<Alert> alerts = alertRepo.findAll();
        return alerts.stream()
                .map(this::from)
                .toList();
    }

    public List<AlertResponse> findByResolved() {

        List<Alert> alerts = alertRepo.findByResolvedFalseOrderByCreatedAtDesc();
        return alerts.stream()
                .map(this::from)
                .toList();
    }

    public AlertResponse from(Alert a) {
        AlertResponse r = new AlertResponse();
        r.setId(a.getId());
        r.setType(a.getSensor().getType().name());
        r.setMessage(a.getMessage());
        r.setSensorId(a.getSensor().getId());
        r.setLevel(a.getLevel().name());
        r.setResolved(a.isResolved());
        r.setCreatedAt(a.getCreatedAt());
        r.setResolvedAt(a.getResolvedAt());
        return r;
    }

    @Transactional
    public SuccessResponse<?> resolve(String id) {

        Alert a = alertRepo.findById(id).orElseThrow();

        a.setResolved(true);
        a.setResolvedAt(LocalDateTime.now());

        // Resolve all children too
        alertRepo.resolveAllByParent(a.getId());

        if (a.getParent() != null)
            alertRepo.resolveAllByParentAndChild(a.getParent().getId());
        alertRepo.save(a);
        return new SuccessResponse<>(
                HttpStatus.OK.name(),
                "Successful",
                null
        );
    }


}
