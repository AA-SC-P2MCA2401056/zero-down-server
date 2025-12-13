package com.server.zero_down.Service;

import com.server.zero_down.Common.Enums.SensorType;
import com.server.zero_down.Dto.View.SensorCard;
import com.server.zero_down.Dto.View.SensorHistoryPoint;
import com.server.zero_down.Modal.Sensor;
import com.server.zero_down.Modal.SensorReading;
import com.server.zero_down.Repository.SensorReadingRepository;
import com.server.zero_down.Repository.SensorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final SensorRepository sensorRepository;
    private final SensorReadingRepository sensorReadingRepository;

    public List<SensorCard> getMainDashboardCards() {
        List<SensorCard> result = new ArrayList<>();

        for (SensorType type : SensorType.values()) {
            // assume one primary sensor per type for now
            List<Sensor> sensors = sensorRepository.findByType(type);
            if (sensors.isEmpty()) {
                continue;
            }
            Sensor sensor = sensors.getFirst();

            SensorReading latest = sensorReadingRepository
                    .findTopBySensorIdOrderByRecordedAtDesc(sensor.getId())
                    .orElse(null);

            SensorCard dto = new SensorCard();
            dto.setSensorId(sensor.getId());
            dto.setName(sensor.getName());
            dto.setType(sensor.getType());

            if (latest != null) {
                dto.setValue(latest.getValue());
                dto.setRecordedAt(latest.getRecordedAt());
            }

            result.add(dto);
        }

        return result;
    }

    public List<SensorHistoryPoint> getHistory(int minutes) {

        // 1️⃣ pick one sensor per type
        Map<SensorType, Sensor> sensorsByType = new EnumMap<>(SensorType.class);

        for (SensorType type : SensorType.values()) {
            List<Sensor> sensors = sensorRepository.findByType(type);
            if (!sensors.isEmpty()) {
                sensorsByType.put(type, sensors.getFirst());
            }
        }

        // 2️⃣ timeMillis → SensorHistoryPoint (sorted)
        Map<Long, SensorHistoryPoint> points = new TreeMap<>();

        for (Map.Entry<SensorType, Sensor> entry : sensorsByType.entrySet()) {
            SensorType type = entry.getKey();
            Sensor sensor = entry.getValue();

            List<SensorReading> readings = sensorReadingRepository.findBySensorId(sensor.getId());

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

        // 3️⃣ sorted list (oldest → newest)
        List<SensorHistoryPoint> list = new ArrayList<>(points.values());
        if (list.isEmpty()) {
            return list;
        }

        // ❌ throw away the minutes logic
        // ✅ just keep the last 30 entries (or whatever you pass)

        int maxPoints = 30; // or use the method parameter instead of hardcoding

        if (list.size() > maxPoints) {
            // last 'maxPoints' elements (still sorted ascending by time)
            return list.subList(list.size() - maxPoints, list.size());
        }

        return list;
    }




    public SensorHistoryPoint getLatestSnapshot() {
        SensorHistoryPoint snapshot= new SensorHistoryPoint();
        sensorReadingRepository
                .findFirstBySensor_TypeAndSensor_ActiveOrderByRecordedAtDesc(
                        SensorType.TEMPERATURE, true
                )
                .ifPresent(r -> snapshot.setTemp(r.getValue()));

        sensorReadingRepository
                .findFirstBySensor_TypeAndSensor_ActiveOrderByRecordedAtDesc(
                        SensorType.HUMIDITY, true
                )
                .ifPresent(r -> snapshot.setHumidity(r.getValue()));

        sensorReadingRepository
                .findFirstBySensor_TypeAndSensor_ActiveOrderByRecordedAtDesc(
                        SensorType.SOIL_MOISTURE, true
                )
                .ifPresent(r -> snapshot.setSoil(r.getValue()));

        sensorReadingRepository
                .findFirstBySensor_TypeAndSensor_ActiveOrderByRecordedAtDesc(
                        SensorType.LIGHT, true
                )
                .ifPresent(r -> snapshot.setLight(r.getValue()));

        return snapshot;
    }
}

