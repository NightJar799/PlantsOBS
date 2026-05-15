package org.plrest.repositories;

import org.plrest.entity.PlantSample;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlantSampleRepository extends JpaRepository<PlantSample, Long> {
}
