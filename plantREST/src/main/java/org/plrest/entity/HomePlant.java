package org.plrest.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.obs.dto.HomePlantRequest;

import java.util.List;

@Entity
@Table(name = "home_plants")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HomePlant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "note", nullable = false, length = 500)
    private String note;

    @Column(name = "species", nullable = false, length = 200)
    private String species;

    @Column(name = "age", nullable = false)
    private Integer age;

    @ManyToOne
    @JoinColumn(name = "plant_sample_id")
    private PlantSample plantSample;

    @OneToMany(mappedBy = "robots", cascade = CascadeType.ALL)
    private List<Robot> robots;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private User user;


    public static HomePlant fromRequest(HomePlantRequest request, PlantSample plantSample, List<Robot> robots, User user) {
        HomePlant homePlant = new HomePlant();
        homePlant.setId(request.id());
        homePlant.setName(request.name());
        homePlant.setNote(request.note());
        homePlant.setSpecies(request.species());
        homePlant.setAge(request.age());
        homePlant.setPlantSample(plantSample);
        homePlant.setRobots(robots);
        homePlant.setUser(user);
        return homePlant;
    }
}