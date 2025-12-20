package com.server.zero_down.Service;

import com.server.zero_down.Common.Enums.SensorType;
import com.server.zero_down.Dto.Forms.SensorReadingRequest;
import com.server.zero_down.Dto.View.SensorHistoryPoint;
import com.server.zero_down.Modal.Sensor;
import com.server.zero_down.Modal.SensorReading;
import com.server.zero_down.Repository.SensorReadingRepository;
import com.server.zero_down.Repository.SensorRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
@RequiredArgsConstructor
public class SensorReadingService {

    private final SensorRepository sensorRepository;
    private final SensorReadingRepository sensorReadingRepository;

    @Transactional
    public void saveReading(SensorReadingRequest request) {
        Optional<Sensor> opt = sensorRepository.findById(request.getSensorId());

        if (opt.isEmpty()) {
            System.out.println("Skipping unknown sensor: " + request.getSensorId());
            return;
        }

        if (sensorReadingRepository.existsBySensorIdAndRecordedAt(
                request.getSensorId(),
                request.getRecordedAt()
        )) {
            System.out.println("Duplicate skipped for sensor  " + request.getSensorId());
            return;
        }


        Sensor sensor = opt.get();
        SensorReading reading = new SensorReading();
        reading.setSensor(sensor);
        reading.setValue(request.getValue());
        reading.setRecordedAt(
                request.getRecordedAt() != null ? request.getRecordedAt() : LocalDateTime.now()
        );

        sensorReadingRepository.save(reading);
    }

    public List<SensorHistoryPoint> getHistory(int minutes) {

        LocalDateTime to = LocalDateTime.now();
        LocalDateTime from = to.minusMinutes(minutes);

        // 1️⃣ get one sensor per type
        Map<SensorType, Sensor> sensorsByType = new EnumMap<>(SensorType.class);

        for (SensorType type : SensorType.values()) {
            List<Sensor> sensors = sensorRepository.findByType(type);
            if (!sensors.isEmpty()) {
                sensorsByType.put(type, sensors.getFirst()); // first sensor of that type
            }
        }

        // 2️⃣ time → SensorHistoryPoint map
        Map<Long, SensorHistoryPoint> points = new TreeMap<>(); // sorted by time asc

        for (Map.Entry<SensorType, Sensor> entry : sensorsByType.entrySet()) {
            SensorType type = entry.getKey();
            Sensor sensor = entry.getValue();

            List<SensorReading> readings = sensorReadingRepository
                    .findBySensorId(
                            sensor.getId()
                    );

            for (SensorReading reading : readings) {
                long timeMillis = reading.getRecordedAt()
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli();

                SensorHistoryPoint point = points.computeIfAbsent(timeMillis, t -> {
                    SensorHistoryPoint p = new SensorHistoryPoint();
                    p.setTime(t);
                    return p;
                });
                point.setDate(reading.getRecordedAt().toLocalDate());

                switch (type) {
                    case TEMPERATURE -> point.setTemp(reading.getValue());
                    case HUMIDITY -> point.setHumidity(reading.getValue());
                    case SOIL_MOISTURE -> point.setSoil(reading.getValue());
                    case LIGHT -> point.setLight(reading.getValue());
                }
            }
        }

        // 3️⃣ convert map to list (sorted by time), and you can limit to last 20 if you want
        return new ArrayList<>(points.values());
    }
}

