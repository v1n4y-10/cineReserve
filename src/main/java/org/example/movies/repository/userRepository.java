
package org.example.movies.repository;

import org.example.movies.entity.user;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface userRepository extends JpaRepository<user, Long> {

    Optional<user> findByEmail(String email);

}
