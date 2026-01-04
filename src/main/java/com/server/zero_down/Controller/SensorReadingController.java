package com.server.zero_down.Controller;

import com.server.zero_down.Dto.Forms.SensorReadingRequest;
import com.server.zero_down.Dto.View.LogPaginatedList;
import com.server.zero_down.Dto.View.SensorHistoryPoint;
import com.server.zero_down.Service.SensorReadingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sensor-readings")
@RequiredArgsConstructor
public class SensorReadingController {

    private final SensorReadingService sensorReadingService;

    @PostMapping
    public ResponseEntity<Void> createReading(@RequestBody SensorReadingRequest request) {
        sensorReadingService.saveReading(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/history")
    public LogPaginatedList<SensorHistoryPoint> getHistory(
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String to,
            @RequestParam(required = false) String from
    ) {
        return sensorReadingService.getHistory(page, size, to, from);
    }
}

