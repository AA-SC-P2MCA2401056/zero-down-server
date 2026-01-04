package com.server.zero_down.Service;

import com.server.zero_down.Dto.Forms.SensorCreateRequest;
import com.server.zero_down.Dto.Forms.SensorUpdateRequest;
import com.server.zero_down.Dto.View.LogPaginatedList;
import com.server.zero_down.Dto.View.SensorLogResponse;
import com.server.zero_down.Dto.View.SensorResponse;
import com.server.zero_down.Dto.View.SensorSummary;
import com.server.zero_down.Modal.Sensor;
import com.server.zero_down.Modal.SensorReading;
import com.server.zero_down.Repository.SensorReadingRepository;
import com.server.zero_down.Repository.SensorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminSensorService {

    private final SensorRepository sensorRepository;
    private final SensorReadingRepository sensorLogRepository;

    public SensorResponse createSensor(SensorCreateRequest request) {

        String prefix = switch (request.getType()) {
            case TEMPERATURE -> "TEMP";
            case SOIL_MOISTURE -> "SOIL";
            case LIGHT -> "LGHT";
            case HUMIDITY -> "HMDT";
        };

        String generatedId = prefix + "00" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        Sensor sensor = new Sensor();
        sensor.setId(generatedId);
        sensor.setName(request.getName());
        sensor.setType(request.getType());
        sensor.setLocation(request.getLocation());

        Sensor saved = sensorRepository.save(sensor);

        return new SensorResponse(
                saved.getId(),
                saved.getName(),
                saved.getType(),
                saved.getLocation(),
                saved.isActive(),
                saved.getUpdatedAt(),
                null,
                saved.getCreatedAt(),
                null
        );
    }

    public List<SensorResponse> getAllSensors() {
        return sensorRepository.findAll()
                .stream()
                .map(s -> new SensorResponse(
                        s.getId(),
                        s.getName(),
                        s.getType(),
                        s.getLocation(),
                        s.isActive(),
                        s.getUpdatedAt(),
                        null,
                        s.getCreatedAt(),
                        null
                ))
                .toList();
    }


    public SensorResponse updateSensor(String id, SensorUpdateRequest request) {
        Sensor sensor = new Sensor();
        try {
            Optional<Sensor> opt = sensorRepository.findById(id);
            if (opt.isPresent()) {
                sensor = opt.get();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        sensor.setName(request.getName());
        sensor.setLocation(request.getLocation());
        sensor.setPinNumber(request.getPinNumber());
        sensor.setUnit(request.getUnit());
        sensor.setActive(request.getActive() != null ? request.getActive() : sensor.isActive());
        if (request.getType() != null) {
            sensor.setType(request.getType());
        }

        Sensor saved = sensorRepository.save(sensor);
        return new SensorResponse(
                saved.getId(),
                saved.getName(),
                saved.getType(),
                saved.getLocation(),
                saved.isActive(),
                saved.getUpdatedAt(),
                null,
                saved.getCreatedAt(),
                null
        );
    }

    public void deleteSensor(String id)  {
        if (!sensorRepository.existsById(id)) {
            throw new RuntimeException();
        }
        sensorRepository.deleteById(id);
    }

    public LogPaginatedList<SensorLogResponse> findBySensor_SensorId(String sensorId, PageRequest pageable, String from, String to) {

        Page<SensorReading> page = sensorLogRepository.findBySensor_IdAndRecordedAtBetween(sensorId,
                LocalDateTime.parse(from),
                LocalDateTime.parse(to),
                pageable);

        List<SensorLogResponse> logs = page.getContent().stream().map(r -> {
            SensorLogResponse d = new SensorLogResponse();
            d.setId(r.getId());
            d.setValue(r.getValue());
            d.setRecordedAt(r.getRecordedAt());
            return d;
        }).toList();

        return new LogPaginatedList<>(
                page.getNumber(),
                page.getSize(),
                page.getTotalPages(),
                logs
        );
    }


    public List<SensorLogResponse> last24h(String sensorId, LocalDateTime from, LocalDateTime to) {

        // If no date filter – default to today 00:00 -> now
        if (from == null) {
            from = LocalDateTime.now().toLocalDate().atStartOfDay();
        }
        if (to == null) {
            to = LocalDateTime.now();
        }
        List<SensorReading> recording = sensorLogRepository.findLast24Hours(sensorId,
                from.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                to.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return
                recording.stream()
                .map(r -> {
                    SensorLogResponse p = new SensorLogResponse();
                    p.setId(r.getId());
                    p.setValue(r.getValue());
                    p.setRecordedAt(r.getRecordedAt());
                    p.setRecordedTime(r.getRecordedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                    return p;
                })
                .toList();
    }


    public SensorSummary summary(String id, LocalDateTime from, LocalDateTime to){
        // If no date filter – default to today 00:00 -> now
        if (from == null) {
            from = LocalDateTime.now().toLocalDate().atStartOfDay();
        }
        if (to == null) {
            to = LocalDateTime.now();
        }
        List<SensorReading> r = sensorLogRepository.findLast24Hours(id,
                from.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                to.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        if (r.isEmpty()) {
            SensorSummary empty = new SensorSummary();
            empty.setCount(0L);
            return empty;
        }

        DoubleSummaryStatistics s = r.stream()
                .mapToDouble(SensorReading::getValue)
                .summaryStatistics();

        SensorSummary d = new SensorSummary();
        d.setMin(s.getMin());
        d.setMax(s.getMax());
        d.setAvg(s.getAverage());
        d.setCount(s.getCount());
        d.setStable((s.getMax() - s.getMin()) < 5); // jitter check

        // find exact timestamps
        r.stream().filter(x -> x.getValue() == s.getMin()).findFirst()
                .ifPresent(x -> d.setMinTime(x.getRecordedAt()));

        r.stream().filter(x -> x.getValue() == s.getMax()).findFirst()
                .ifPresent(x -> d.setMaxTime(x.getRecordedAt()));
        return d;
    }

    public SensorResponse getMeta(String id) {
        Sensor s = sensorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sensor not found"));

        SensorReading lastReading = sensorLogRepository.findLastReading(id);

        SensorResponse r = new SensorResponse();
        r.setId(s.getId());
        r.setName(s.getName());
        r.setType(s.getType());
        r.setCreatedAt(s.getCreatedAt());
        r.setUpdatedAt(s.getUpdatedAt());
        r.setLastLiveAt(lastReading.getRecordedAt());

        // online logic (last 40 sec)
        r.setOnline(
                lastReading.getRecordedAt() != null &&
                        Duration.between(lastReading.getRecordedAt(), LocalDateTime.now()).getSeconds() < 40
        );

        return r;
    }
}

