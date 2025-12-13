package com.server.zero_down.Controller;

import com.server.zero_down.Dto.View.SensorCard;
import com.server.zero_down.Dto.View.SensorHistoryPoint;
import com.server.zero_down.Service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/main")
    public List<SensorCard> getMainDashboard() {
        return dashboardService.getMainDashboardCards();
    }

    @GetMapping("/history")
    public List<SensorHistoryPoint> getHistory(
            @RequestParam(defaultValue = "20") int minutes
    ) {
        return dashboardService.getHistory(minutes);
    }

    @GetMapping("/snapshot")
    public SensorHistoryPoint getLatestSnapshot() {
        return dashboardService.getLatestSnapshot();
    }
}

