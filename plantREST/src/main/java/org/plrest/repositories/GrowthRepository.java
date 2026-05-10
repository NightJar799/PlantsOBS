package org.plrest.repositories;

import org.obs.entity.GrowthCharacteristic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GrowthRepository extends JpaRepository<GrowthCharacteristic, Long> {
    Optional<GrowthCharacteristic> findTopByHomePlantIdOrderByRecordedAtDesc(Long plantId);
}
