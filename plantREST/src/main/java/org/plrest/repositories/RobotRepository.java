package org.plrest.repositories;

import org.obs.entity.Robot;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RobotRepository extends JpaRepository<Robot, Long> {
    // Поиск роботов по ID растения
    List<Robot> findByHomePlantId(Long homePlantId);
    Page<Robot> findByHomePlantIdPage(Long homePlantId, Pageable pageable);

    // Проверка существования робота с таким именем у растения
    boolean existsByNameAndHomePlantId(String name, Long homePlantId);

    // Удаление всех роботов растения
    void deleteByHomePlantId(Long homePlantId);
}
