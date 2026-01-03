package com.server.zero_down.Service;

import com.server.zero_down.Dto.Forms.SensorCreateRequest;
import com.server.zero_down.Dto.Forms.SensorUpdateRequest;
import com.server.zero_down.Dto.View.SensorResponse;
import com.server.zero_down.Modal.Sensor;
import com.server.zero_down.Repository.SensorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminSensorService {

    private final SensorRepository sensorRepository;

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
                saved.getLocation()
        );
    }

    public List<SensorResponse> getAllSensors() {
        return sensorRepository.findAll()
                .stream()
                .map(s -> new SensorResponse(
                        s.getId(),
                        s.getName(),
                        s.getType(),
                        s.getLocation()
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
                saved.getLocation()
        );
    }

    public void deleteSensor(String id)  {
        if (!sensorRepository.existsById(id)) {
            throw new RuntimeException();
        }
        sensorRepository.deleteById(id);
    }
}

