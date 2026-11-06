package com.fleet.repo;

import com.fleet.model.Car;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CarRepository extends JpaRepository<Car, Long> {

    List<Car> findByActiveTrueOrderByMatriculeAsc();

    Optional<Car> findByIdAndActiveTrue(Long id);

    Optional<Car> findByMatricule(String matricule);

    /** Active cars, not in maintenance, with no ACTIVE attribution overlapping [start, end). */
    @Query("""
        select c from Car c
        where c.active = true
          and c.status = com.fleet.model.CarStatus.AVAILABLE
          and c.id not in (
              select a.car.id from Attribution a
              where a.status = com.fleet.model.AttributionStatus.ACTIVE
                and a.startAt < :end and a.endAt > :start)
        order by c.matricule
        """)
    List<Car> findAvailable(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
