package org.example.movies.repository;

import org.example.movies.entity.theater;
import org.springframework.data.jpa.repository.JpaRepository;

public interface theaterRepository extends JpaRepository<theater, Long>{
    Long id(long id);

}
