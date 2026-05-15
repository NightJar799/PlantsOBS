package org.plrest.storage;

import org.obs.dto.*;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
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

    @PostConstruct
    public void init() {
        Long sampleId = plantSampleSequence.incrementAndGet();
        PlantsSampleResponse plantSample = PlantsSampleResponse.builder()
                .id(sampleId)
                .type("Тропическое")
                .fruiting("Нет")
                .flower("Да")
                .difficulty(3)
                .wikiUrl("https://ru.wikipedia.org/wiki/Фикус")
                .build();
        plantSamples.put(sampleId, plantSample);

        Long homePlantId = homePlantSequence.incrementAndGet();
        HomePlantResponse homePlant = HomePlantResponse.builder()
                .id(homePlantId)
                .sampleId(sampleId)
                .name("Мой фикус Бенджамина")
                .note("Поливать 1 раз в неделю, опрыскивать листья")
                .species("Ficus benjamina")
                .age(2)
                .build();
        homePlants.put(homePlantId, homePlant);

        Long growthCharId = growthCharSequence.incrementAndGet();
        GrowthCharResponse growthChar = GrowthCharResponse.builder()
                .id(growthCharId)
                .lx(8000)
                .water(70)
                .heat(24)
                .air(420)
                .nitrogen(25)
                .soilPh(6.2)
                .humidity("60%")
                .build();
        growthChars.put(growthCharId, growthChar);

        Long robotId1 = robotSequence.incrementAndGet();
        RobotResponse robot1 = RobotResponse.builder()
                .id(robotId1)
                .plantId(homePlantId)
                .name("Термодатчик-1")
                .sensorType(1)
                .measuredCharacteristic("Температура воздуха")
                .usedCharacteristic("heat")
                .build();
        robots.put(robotId1, robot1);

        Long robotId2 = robotSequence.incrementAndGet();
        RobotResponse robot2 = RobotResponse.builder()
                .id(robotId2)
                .plantId(homePlantId)
                .name("Влагодатчик-1")
                .sensorType(2)
                .measuredCharacteristic("Влажность почвы")
                .usedCharacteristic("water")
                .build();
        robots.put(robotId2, robot2);

        ConcurrentHashMap<Long, RobotResponse> robotsForPlant = new ConcurrentHashMap<>();
        robotsForPlant.put(robotId1, robot1);
        robotsForPlant.put(robotId2, robot2);
        plantRobots.put(homePlantId, robotsForPlant);
        robotLatestGrowthChar.put(robotId1, growthCharId);
        robotLatestGrowthChar.put(robotId2, growthCharId);
    }
}