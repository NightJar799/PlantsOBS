package org.plrest.repositories;

import org.plrest.entity.HomePlant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HomePlantRepository extends JpaRepository<HomePlant, Long> {
}
