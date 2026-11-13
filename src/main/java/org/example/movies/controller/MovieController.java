package org.example.movies.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.example.movies.entity.movie;
import org.example.movies.repository.repository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;
import java.util.Optional;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/movies")
public class MovieController {

    private final repository movieRepository;

    public MovieController(repository movieRepository) {
        this.movieRepository = movieRepository;
    }

    @GetMapping("")
    public List<movie> getAllMovies() {
        return movieRepository.findAll();
    }

    @PostMapping
    public movie addMovie(@RequestBody movie newMovie) {
        return movieRepository.save(newMovie);
    }

    @GetMapping("/{id}")
    public Optional<movie> getMovieById(@PathVariable Long id) {
        return movieRepository.findById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<movie> updateMovie(
            @PathVariable long id,
            @RequestBody movie updateMovie) {
        return movieRepository.findById(id)
                .map(existingMovie -> {
                    existingMovie.setTitle(updateMovie.getTitle());
                    existingMovie.setGenre(updateMovie.getGenre());
                    existingMovie.setDuration(updateMovie.getDuration());
                    existingMovie.setLanguage(updateMovie.getLanguage());

                    movie savedMovie = movieRepository.save(existingMovie);
                    return ResponseEntity.ok(savedMovie);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable Long id) {

        if (!movieRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        movieRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}