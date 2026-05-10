package org.plrest.storage;

import org.obs.dto.*;
import org.springframework.stereotype.Component;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class InMemoryStorage {

    public final ConcurrentHashMap<Long, PlantsSampleResponse> plantSamples = new ConcurrentHashMap<>();
    public final ConcurrentHashMap<Long, HomePlantResponse> homePlants = new ConcurrentHashMap<>();
    public final ConcurrentHashMap<Long, GrowthCharResponse> growthChars = new ConcurrentHashMap<>();
    public final ConcurrentHashMap<Long, RobotResponse> robots = new ConcurrentHashMap<>();

    public final ConcurrentHashMap<Long, ConcurrentHashMap<Long, RobotResponse>> plantRobots = new ConcurrentHashMap<>();

    public final ConcurrentHashMap<Long, Long> robotLatestGrowthChar = new ConcurrentHashMap<>();

    public final AtomicLong plantSampleSequence = new AtomicLong(0);
    public final AtomicLong homePlantSequence = new AtomicLong(0);
    public final AtomicLong growthCharSequence = new AtomicLong(0);
    public final AtomicLong robotSequence = new AtomicLong(0);
}