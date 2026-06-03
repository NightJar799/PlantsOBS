package org.grpc.plant.service;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import org.robotContract.grpc.RobotCustomisationRequest;
import org.robotContract.grpc.RobotCustomisationResponse;
import org.robotContract.grpc.RobotCustomiseGrpc;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RobotCustomiseServiceImpl extends RobotCustomiseGrpc.RobotCustomiseImplBase {

    private static final Logger log = LoggerFactory.getLogger(RobotCustomiseServiceImpl.class);

    private final Map<Long, Set<String>> usedMetricsPerPlant = new ConcurrentHashMap<>();

     @Override
    public void changeRobotMeasurment(RobotCustomisationRequest request,
                                      StreamObserver<RobotCustomisationResponse> responseObserver) {

        long plantId = request.getPlantId();
        String requestedMetric = request.getChosenMeasurment().isBlank()
                ? request.getMeasurment()   // для created — предлагаем то, что датчик умеет
                : request.getChosenMeasurment();

        log.info("gRPC запрос: robotId={}, plantId={}, requestedMetric={}", 
                request.getRobotId(), plantId, requestedMetric);

        Set<String> used = usedMetricsPerPlant.computeIfAbsent(plantId, k -> new CopyOnWriteArraySet<>());

        String chosen = "";
        if (!requestedMetric.isBlank() && !used.contains(requestedMetric)) {
            used.add(requestedMetric);
            chosen = requestedMetric;
            log.info("Метрика '{}' добавлена для растения {}", chosen, plantId);
        } else if (used.contains(requestedMetric)) {
            log.warn("Метрика '{}' уже используется для растения {}", requestedMetric, plantId);
        }

        RobotCustomisationResponse response = RobotCustomisationResponse.newBuilder()
                .setRobotId(request.getRobotId())
                .setName(request.getName())
                .setMeasurment(request.getMeasurment())
                .setPlantId(plantId)
                .setChosenMeasurment(chosen)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
