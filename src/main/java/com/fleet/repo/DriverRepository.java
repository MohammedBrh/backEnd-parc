package com.fleet.repo;

import com.fleet.model.Driver;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DriverRepository extends JpaRepository<Driver, Long> {

    List<Driver> findByActiveTrueOrderByNameAsc();

    Optional<Driver> findByIdAndActiveTrue(Long id);

    @Query("""
        select d from Driver d
        where d.active = true
          and d.status = com.fleet.model.DriverStatus.AVAILABLE
          and d.id not in (
              select a.driver.id from Attribution a
              where a.status = com.fleet.model.AttributionStatus.ACTIVE
                and a.startAt < :end and a.endAt > :start)
        order by d.name
        """)
    List<Driver> findAvailable(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
