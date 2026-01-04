package com.server.zero_down.Controller;

import com.server.zero_down.Dto.View.LogPaginatedList;
import com.server.zero_down.Dto.View.SensorLogResponse;
import com.server.zero_down.Dto.View.SensorSummary;
import com.server.zero_down.Service.AdminSensorService;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/admin/sensors-log")
@RequiredArgsConstructor
public class AdminSensorLogController {

    private final AdminSensorService adminSensorService;

    @GetMapping("/{sensorId}/logs")
    public LogPaginatedList<?> getLogs(
            @PathVariable String sensorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) {

        if (StringUtils.isBlank(from)) {
            from = String.valueOf(LocalDateTime.now().toLocalDate().atStartOfDay());
        }
        if (StringUtils.isBlank(to)) {
            to = String.valueOf(LocalDateTime.now());
        }
        return adminSensorService.findBySensor_SensorId(sensorId,
                PageRequest.of(page, size, Sort.by("recordedAt").descending()),
                from, to);
    }

    @GetMapping("/{sensorId}/chart")
    public List<SensorLogResponse> chart(@PathVariable String sensorId,
                                         @RequestParam(required = false) String from,
                                         @RequestParam(required = false) String to) {
        // If no date filter – default to today 00:00 -> now
        if (StringUtils.isBlank(from)) {
            from = String.valueOf(LocalDateTime.now().toLocalDate().atStartOfDay());
        }
        if (StringUtils.isBlank(to)) {
            to = String.valueOf(LocalDateTime.now());
        }
        return adminSensorService.last24h(sensorId, LocalDateTime.parse(from),
                LocalDateTime.parse(to));
    }

    @GetMapping("/{sensorId}/summary")
    public SensorSummary summary(
            @PathVariable String sensorId,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to
    ) {
        // If no date filter – default to today 00:00 -> now
        if (StringUtils.isBlank(from)) {
            from = String.valueOf(LocalDateTime.now().toLocalDate().atStartOfDay());
        }
        if (StringUtils.isBlank(to)) {
            to = String.valueOf(LocalDateTime.now());
        }
        return adminSensorService.summary(sensorId,
                LocalDateTime.parse(from),
                LocalDateTime.parse(to)
        );
    }

}

