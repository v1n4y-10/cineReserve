package org.example.movies.controller;

import org.example.movies.entity.theater;
import org.example.movies.repository.theaterRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.stream.*;

@RestController
@RequestMapping("/api/theater")
public class TheaterController {
    private final theaterRepository theaterRepository;

    public TheaterController(theaterRepository theaterRepository) {
        this.theaterRepository = theaterRepository;
    }

    @GetMapping
    public List<theater> getAllTheatres() {
        return theaterRepository.findAll();
    }

    @PostMapping
    public theater addTheatre(@RequestBody theater newTheater) {
        return theaterRepository.save(newTheater);
    }

    @GetMapping("/{id}")
    public Optional<theater> getTheaterById(@PathVariable Long id){return theaterRepository.findById(id);}

    @PutMapping("/{id}")
    public ResponseEntity<theater> updateTheater(
            @PathVariable Long id,
            @RequestBody theater updateTheater) {
        return theaterRepository.findById(id)
                .map(existingTheater ->{
                    existingTheater.setName(updateTheater.getName());
                    existingTheater.setAddress(updateTheater.getAddress());
                    existingTheater.setCity(updateTheater.getCity());

                    theater savedTheater=theaterRepository.save(existingTheater);
                    return ResponseEntity.ok(savedTheater);
        })
        .orElseGet(()-> ResponseEntity.notFound().build());
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTheater(@PathVariable Long id) {
        if (!theaterRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        theaterRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
