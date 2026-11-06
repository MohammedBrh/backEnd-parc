package com.fleet.repo;

import com.fleet.model.Attribution;
import com.fleet.model.AttributionStatus;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AttributionRepository extends JpaRepository<Attribution, Long> {

    List<Attribution> findByStatusOrderByStartAtAsc(AttributionStatus status);

    List<Attribution> findAllByOrderByStartAtDesc();

    @Query("""
        select count(a) from Attribution a
        where a.status = com.fleet.model.AttributionStatus.ACTIVE
          and a.car.id = :id and a.startAt < :end and a.endAt > :start
        """)
    long countCarOverlap(@Param("id") Long id, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("""
        select count(a) from Attribution a
        where a.status = com.fleet.model.AttributionStatus.ACTIVE
          and a.driver.id = :id and a.startAt < :end and a.endAt > :start
        """)
    long countDriverOverlap(@Param("id") Long id, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("""
        select count(a) from Attribution a
        where a.status = com.fleet.model.AttributionStatus.ACTIVE
          and a.personnel.id = :id and a.startAt < :end and a.endAt > :start
        """)
    long countPersonnelOverlap(@Param("id") Long id, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    boolean existsByCarIdAndStatusAndEndAtAfter(Long carId, AttributionStatus status, LocalDateTime now);

    boolean existsByDriverIdAndStatusAndEndAtAfter(Long driverId, AttributionStatus status, LocalDateTime now);

    boolean existsByPersonnelIdAndStatusAndEndAtAfter(Long personnelId, AttributionStatus status, LocalDateTime now);
}
