package com.server.zero_down.Controller;

import com.server.zero_down.Dto.View.AlertResponse;
import com.server.zero_down.Dto.Forms.SensorReadingRequest;
import com.server.zero_down.Dto.View.SuccessResponse;
import com.server.zero_down.Service.AlertEngineService;
import com.server.zero_down.Service.SensorReadingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alert")
@RequiredArgsConstructor
public class AlertController {

    private final SensorReadingService sensorReadingService;
    private final AlertEngineService alertEngine;

    @PostMapping("/readings")
    public void save(@RequestBody SensorReadingRequest r){
        sensorReadingService.saveReading(r);
        alertEngine.evaluate(r);
    }

    @GetMapping("/active")
    public List<AlertResponse> getActive(){
        return alertEngine.findByResolved();
    }

    @GetMapping("/history")
    public List<AlertResponse> getHistory(){
        return alertEngine.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    @PutMapping("/{id}/resolve")
    public ResponseEntity<?> resolve(@PathVariable String id) {
        SuccessResponse<?> response = alertEngine.resolve(id);
        return ResponseEntity.ok().body(response);
    }

}
