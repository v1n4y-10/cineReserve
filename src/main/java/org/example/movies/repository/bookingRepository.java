
package org.example.movies.repository;

import org.example.movies.entity.booking;
import java.util.List;

import org.example.movies.entity.booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface bookingRepository extends JpaRepository<booking, Long> {
    List<booking> findByUser_Id(Long userId);
}
