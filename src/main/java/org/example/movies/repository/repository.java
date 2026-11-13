package org.example.movies.repository;
import org.example.movies.entity.movie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface repository extends JpaRepository<movie, Long>{
}
