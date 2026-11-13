
package org.example.movies.controller;

import org.example.movies.entity.show;
import org.example.movies.repository.showRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shows")
public class ShowController {

    private final showRepository showRepository;

    public ShowController(showRepository showRepository) {
        this.showRepository = showRepository;
    }

    // Get all shows
    @GetMapping
    public List<show> getAllShows() {
        return showRepository.findAll();
    }

    // Get show by ID
    @GetMapping("/{id}")
    public ResponseEntity<show> getShowById(@PathVariable Long id) {
        return showRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Add a show
    @PostMapping
    public show addShow(@RequestBody show show) {
        return showRepository.save(show);
    }

    // Update a show
    @PutMapping("/{id}")
    public ResponseEntity<show> updateShow(
            @PathVariable Long id,
            @RequestBody show updatedShow) {

        return showRepository.findById(id)
                .map(existingShow -> {
                    existingShow.setMovie(updatedShow.getMovie());
                    existingShow.setScreen(updatedShow.getScreen());
                    existingShow.setStartTime(updatedShow.getStartTime());
                    existingShow.setTicketPrice(updatedShow.getTicketPrice());

                    return ResponseEntity.ok(
                            showRepository.save(existingShow)
                    );
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Delete a show
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShow(@PathVariable Long id) {
        if (!showRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        showRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
