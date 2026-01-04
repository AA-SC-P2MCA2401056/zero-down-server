package com.server.zero_down.Common.Handler.Scheduler;

import com.google.firebase.database.*;
import com.server.zero_down.Dto.Forms.SensorReadingRequest;
import com.server.zero_down.Service.AlertEngineService;
import com.server.zero_down.Service.SensorReadingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

@Slf4j
@Service
@RequiredArgsConstructor
public class FirebaseAlertScheduler {


    private final ExecutorService executor;
    private final AlertEngineService alertEngine;

    // ⏱ every 10 sec
    @Scheduled(cron = "*/10 * * * * *")
    public void captureSnapshot() {

        try {
            System.out.println("......Starting alert scheduler.....");
            DatabaseReference ref =
                    FirebaseDatabase.getInstance()
                            .getReference("live/esp32_1");

            CountDownLatch latch = new CountDownLatch(1);
            final DataSnapshot[] holder = new DataSnapshot[1];

            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    holder[0] = snapshot;
                    latch.countDown();
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    latch.countDown();
                }
            });

            latch.await();

            DataSnapshot snap = holder[0];

            if (!snap.exists()) return;

            Double temperature = snap.child("temperature").getValue(Double.class);
            Double humidity = snap.child("humidity").getValue(Double.class);
            Double light = snap.child("lightPercent").getValue(Double.class);
            Double soil = snap.child("soil").getValue(Double.class);

            String tempSensorId = snap.child("sensorIdTemp").getValue(String.class);
            String humSensorId = snap.child("sensorIdHum").getValue(String.class);
            String lgtSensorId = snap.child("sensorIdLight").getValue(String.class);
            String soilSensorId = snap.child("sensorIdSoil").getValue(String.class);

            Long timestamp = snap.child("timestamp").getValue(Long.class);

            // consider ESP32 offline if no update in last 40 seconds
            long MAX_ALLOWED_DELAY = 40_000;
            if (temperature == null || humidity == null)
                return;
            if (timestamp == null ||
                    System.currentTimeMillis() - timestamp > MAX_ALLOWED_DELAY) {

                log.warn("ESP32 OFFLINE / STALE — skipping alert evaluation");
                return;
            }
            LocalDateTime recordedAt =
                    LocalDateTime.ofInstant(
                            Instant.ofEpochMilli(timestamp),
                            ZoneId.systemDefault()
                    );

            // -------------------------------
            // 🔥 THREAD 1: TEMPERATURE SENSOR
            // -------------------------------
            executor.submit(() -> {
                System.out.println("Starting thread for TEMP on " + Thread.currentThread().getName());
                SensorReadingRequest tempReq = new SensorReadingRequest();
                tempReq.setSensorId(tempSensorId);
                tempReq.setValue(temperature);
                tempReq.setRecordedAt(recordedAt);

                try {
                    alertEngine.evaluate(tempReq);

                } catch (Exception e) {
                    log.error("TEMP sensor failed", e);
                }
            });

            // -------------------------------
            // 🔥 THREAD 2: HUMIDITY SENSOR
            // -------------------------------
            executor.submit(() -> {
                System.out.println("Starting thread for HUM on " + Thread.currentThread().getName());
                SensorReadingRequest humReq = new SensorReadingRequest();
                humReq.setSensorId(humSensorId);
                humReq.setValue(humidity);
                humReq.setRecordedAt(recordedAt);

                try {
                    alertEngine.evaluate(humReq);
                } catch (Exception e) {
                    log.error("HUM sensor failed", e);
                }
            });

            // -------------------------------
            // 🔥 THREAD 3: LIGHT SENSOR
            // -------------------------------
            executor.submit(() -> {
                System.out.println("Starting thread for LGH on " + Thread.currentThread().getName());
                SensorReadingRequest humReq = new SensorReadingRequest();
                humReq.setSensorId(lgtSensorId);
                humReq.setValue(light);
                humReq.setRecordedAt(recordedAt);

                try {
                    alertEngine.evaluate(humReq);
                } catch (Exception e) {
                    log.error("HUM sensor failed", e);
                }
            });

            // -------------------------------
            // 🔥 THREAD 3: SOIL SENSOR
            // -------------------------------
            executor.submit(() -> {
                System.out.println("Starting thread for SOIL on " + Thread.currentThread().getName());
                SensorReadingRequest humReq = new SensorReadingRequest();
                humReq.setSensorId(soilSensorId);
                humReq.setValue(soil);
                humReq.setRecordedAt(recordedAt);

                try {
                    alertEngine.evaluate(humReq);
                } catch (Exception e) {
                    log.error("SOIL sensor failed", e);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            log.error("alert scheduler for this thread skipped");
        }
    }
}
