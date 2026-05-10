package org.obs.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.obs.dto.PlantSampleRequest;

import java.util.List;

@Entity
@Table(name = "plant_samples")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlantSample {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "type", length = 100)
    private String type;

    @Column(name = "fruiting", length = 10)
    private Boolean isFruiting;

    @Column(name = "flower", length = 10)
    private String flower;

    @Column(name = "difficulty")
    private Integer difficulty;

    @Column(name = "resource_url", nullable = false, length = 500)
    private String resourceUrl;

    @OneToMany(mappedBy = "home_plants", cascade = CascadeType.ALL)
    private List<HomePlant> homePlants;

    @OneToOne(mappedBy = "growth_characteristics", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private GrowthCharacteristic growthCharacteristic;


    public static PlantSample fromRequest(PlantSampleRequest request, List<HomePlant> homePlants, GrowthCharacteristic growthCharacteristic) {
        PlantSample sample = new PlantSample();
        sample.setId(request.id());
        sample.setType(request.type());
        sample.setFlower(request.flower());
        sample.setDifficulty(request.difficulty());
        sample.setHomePlants(homePlants);
        sample.setGrowthCharacteristic(growthCharacteristic);
        return sample;
    }
}