package org.grpc.plant.service;

import org.robotContract.grpc.RobotCustomisationRequest;
import org.robotContract.grpc.RobotCustomisationResponse;
import org.robotContract.grpc.RobotCustomiseGrpc;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class RobotCustomiseServiceImpl extends RobotCustomiseGrpc.RobotCustomiseImplBase {

    private static final Logger log = LoggerFactory.getLogger(RobotCustomiseServiceImpl.class);
    private final Map<Long, Set<String>> usedMetricsPerPlant = new ConcurrentHashMap<>();
    private final Map<Long, Set<String>> robotMetrics = new ConcurrentHashMap<>();

    @Override
    public void changeRobotMeasurment(RobotCustomisationRequest request,
                                      StreamObserver<RobotCustomisationResponse> responseObserver) {

        long robotId = request.getRobotId();
        long plantId = request.getPlantId();

        Set<String> measured = parseMetrics(request.getMeasurment());
        Set<String> currentlyUsed = parseMetrics(request.getChosenMeasurment());

        log.info("gRPC запрос: robotId={}, plantId={}, measured={}, currentlyUsed={}",
                robotId, plantId, measured, currentlyUsed);

        Set<String> used = usedMetricsPerPlant.computeIfAbsent(plantId, k -> ConcurrentHashMap.newKeySet());

        Set<String> oldMetrics = robotMetrics.remove(robotId);
        if (oldMetrics != null && !oldMetrics.isEmpty()) {
            oldMetrics.forEach(used::remove);
            log.debug("Освобождены старые метрики робота {}: {}", robotId, oldMetrics);
        }

        Set<String> newMetrics = new HashSet<>();
        for (String metric : measured) {
            if (!used.contains(metric)) {
                used.add(metric);
                newMetrics.add(metric);
            } else {
                log.debug("Метрика '{}' уже занята для растения {}", metric, plantId);
            }
        }

        if (!newMetrics.isEmpty()) {
            robotMetrics.put(robotId, newMetrics);
        }

        String chosenStr = String.join(", ", newMetrics);

        log.info("Для робота {} выбраны метрики: {}", robotId, chosenStr);

        RobotCustomisationResponse response = RobotCustomisationResponse.newBuilder()
                .setRobotId(robotId)
                .setName(request.getName())
                .setMeasurment(request.getMeasurment())
                .setPlantId(plantId)
                .setChosenMeasurment(chosenStr)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    private Set<String> parseMetrics(String input) {
        if (input == null || input.isBlank()) {
            return Collections.emptySet();
        }
        return Arrays.stream(input.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());
    }
}