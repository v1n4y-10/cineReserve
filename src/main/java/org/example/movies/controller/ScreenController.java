package org.example.movies.controller;

import org.example.movies.entity.screen;
import org.example.movies.repository.screenRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/screens")
public class ScreenController {
    private final screenRepository screenRepository;

    public ScreenController(screenRepository screenRepository) {
        this.screenRepository = screenRepository;
    }

    @GetMapping
    public List<screen> getAllScreens() {
        return screenRepository.findAll();
    }
    @GetMapping("/{id}")
    public ResponseEntity<screen> getScreenById(@PathVariable Long id) {
        return screenRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    @PostMapping
    public screen addScreen(@RequestBody screen screen) {
        return screenRepository.save(screen);
    }

    // Update a screen
    @PutMapping("/{id}")
    public ResponseEntity<screen> updateScreen(
            @PathVariable Long id,
            @RequestBody screen updatedScreen) {

        return screenRepository.findById(id)
                .map(existingScreen -> {
                    existingScreen.setName(updatedScreen.getName());
                    existingScreen.setTotalSeats(updatedScreen.getTotalSeats());
                    existingScreen.setScreenType(updatedScreen.getScreenType());
                    existingScreen.setTheater(updatedScreen.getTheater());

                    return ResponseEntity.ok(
                            screenRepository.save(existingScreen)
                    );
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    // Delete a screen
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteScreen(@PathVariable Long id) {

        if (!screenRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        screenRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
