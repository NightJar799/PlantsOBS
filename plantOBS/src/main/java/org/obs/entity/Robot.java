package org.obs.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.obs.dto.RobotRequest;

@Entity
@Table(name = "robots")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Robot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "sensor_type", nullable = false, length = 50)
    private Integer sensorType;

    @Column(name = "measured_characteristic", nullable = false, length = 100)
    private String measuredCharacteristic;

    @Column(name = "used_characteristic", nullable = false, length = 100)
    private String usedCharacteristics;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "home_plants_id", nullable = false)
    private HomePlant homePlant;

    public static Robot fromRequest(RobotRequest request, HomePlant homePlant) {
        Robot robot = new Robot();
        robot.setId(request.id());
        robot.setName(request.name());
        robot.setSensorType(request.sensorType());
        robot.setMeasuredCharacteristic(request.measuredCharacteristic());
        robot.setUsedCharacteristics(request.usedCharacteristic());
        robot.setHomePlant(homePlant);
        return robot;
    }
}