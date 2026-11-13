
package org.example.movies.repository;

import org.example.movies.entity.show_seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import java.util.Optional;

public interface show_seatRepository extends JpaRepository<show_seat, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM show_seat s WHERE s.id = :id")
    Optional<show_seat> findByIdForUpdate(@Param("id")Long id);
}

