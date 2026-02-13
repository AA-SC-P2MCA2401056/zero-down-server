package com.server.zero_down.Service;

import com.server.zero_down.Dto.Forms.SensorReadingRequest;
import com.server.zero_down.Dto.View.LogPaginatedList;
import com.server.zero_down.Dto.View.SensorHistoryPoint;
import com.server.zero_down.Modal.Sensor;
import com.server.zero_down.Modal.SensorReading;
import com.server.zero_down.Repository.SensorReadingRepository;
import com.server.zero_down.Repository.SensorRepository;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.Tuple;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    public LogPaginatedList<SensorHistoryPoint> getHistory(int page, int size, String to, String from) {

        LocalDateTime toDate = StringUtils.isBlank(to) ? LocalDateTime.now() : LocalDateTime.parse(to);
        LocalDateTime fromDate = StringUtils.isBlank(from) ? LocalDate.now().atStartOfDay() : LocalDateTime.parse(from);

        List<Tuple> rows = sensorReadingRepository.callSensorHistorySP(fromDate, toDate, page, size);

        long total = ((Number) rows.getFirst().get("total")).longValue(); // count result

        List<SensorHistoryPoint> list = new ArrayList<>();
        for (int i = 1; i < rows.size(); i++) {
            Tuple r = rows.get(i);
            SensorHistoryPoint p = new SensorHistoryPoint();

            p.setTime(r.get("time", Number.class).longValue());
            p.setTemp(r.get("temp", Double.class));
            p.setHumidity(r.get("humidity", Double.class));
            p.setSoil(r.get("soil", Double.class));
            p.setLight(r.get("light", Double.class));
            p.setDate(r.get("date", java.sql.Date.class).toLocalDate());

            list.add(p);
        }

        LogPaginatedList<SensorHistoryPoint> res = new LogPaginatedList<>();
        res.setList(list);
        res.setPage(page);
        res.setSize(size);
        res.setTotalPage((int) Math.ceil((double) total / size));
        return res;
    }
}

