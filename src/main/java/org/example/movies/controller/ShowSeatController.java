
package org.example.movies.controller;

import org.example.movies.entity.show_seat;
import org.example.movies.repository.show_seatRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/show-seats")
public class ShowSeatController {

    private final show_seatRepository show_seatRepository;

    public ShowSeatController(show_seatRepository show_seatRepository) {
        this.show_seatRepository = show_seatRepository;
    }

    // Get All Show Seats
    @GetMapping
    public List<show_seat> getAllShowSeats() {
        return show_seatRepository.findAll();
    }

    // Get Show Seat By ID
    @GetMapping("/{id}")
    public ResponseEntity<show_seat> getShowSeatById(@PathVariable Long id) {
        return show_seatRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Create Show Seat
    @PostMapping
    public show_seat addShowSeat(@RequestBody show_seat showSeat) {

        // Every newly created show seat starts as AVAILABLE
        showSeat.setStatus("AVAILABLE");

        return show_seatRepository.save(showSeat);
    }

    // Delete Show Seat
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShowSeat(@PathVariable Long id) {

        show_seat existingSeat = show_seatRepository.findById(id)
                .orElse(null);

        if (existingSeat == null) {
            return ResponseEntity.notFound().build();
        }

        // Do not delete a booked seat
        if ("BOOKED".equals(existingSeat.getStatus())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        show_seatRepository.delete(existingSeat);

        return ResponseEntity.noContent().build();
    }
}
