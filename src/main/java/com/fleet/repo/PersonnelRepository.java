package com.fleet.repo;

import com.fleet.model.Personnel;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonnelRepository extends JpaRepository<Personnel, Long> {

    List<Personnel> findByActiveTrueOrderByNameAsc();

    Optional<Personnel> findByIdAndActiveTrue(Long id);
}
