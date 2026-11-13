package org.example.movies.repository;

import org.example.movies.entity.show;
import org.springframework.data.jpa.repository.JpaRepository;

public interface showRepository extends JpaRepository<show, Long> {
}
