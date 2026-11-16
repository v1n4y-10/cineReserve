
package org.example.movies.repository;

import org.example.movies.entity.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;

import java.util.List;
import java.util.Optional;

public interface bookingRepository extends JpaRepository<booking, Long> {

    // Find all bookings belonging to a user
    List<booking> findByUser_Id(Long userId);

    // Lock a booking during cancellation
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM booking b WHERE b.id = :id")
    Optional<booking> findByIdForUpdate(@Param("id") Long id);
}
