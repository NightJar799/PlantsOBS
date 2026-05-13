package org.plrest.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.obs.dto.GrowthCharRequest;

import java.time.LocalDateTime;

@Entity
@Table(name = "growth_characteristics")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GrowthCharacteristic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "lx", nullable = false)
    private Integer lx;

    @Column(name = "water", nullable = false)
    private Integer water;

    @Column(name = "heat", nullable = false)
    private Integer heat;

    @Column(name = "air", nullable = false)
    private Integer air;

    @Column(name = "nitrogen", nullable = false)
    private Integer nitrogen;

    @Column(name = "soil_ph", nullable = false)
    private Double soilPh;

    @Column(name = "recorded_at", nullable = false)
    private LocalDateTime recordedAt;

    @Column(name = "humidity", length = 7)
    private String humidity;

    @OneToOne
    @JoinColumn(name = "plant_samples_id")
    private PlantSample plantSample;


    public static GrowthCharacteristic fromRequest(GrowthCharRequest request) {
        GrowthCharacteristic gc = new GrowthCharacteristic();
        gc.setId(request.id());
        gc.setLx(request.lx());
        gc.setWater(request.water());
        gc.setHeat(request.heat());
        gc.setAir(request.air());
        gc.setNitrogen(request.nitrogen());
        gc.setSoilPh(request.soilPh());
        gc.setRecordedAt(LocalDateTime.now());
        return gc;
    }
}