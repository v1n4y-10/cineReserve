package org.example.movies.controller;

import org.example.movies.entity.seat;
import org.example.movies.repository.seatRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seats")
public class SeatController {

    private final seatRepository seatRepository;

    public SeatController(seatRepository seatRepository) {
        this.seatRepository = seatRepository;
    }

    @GetMapping
    public List<seat> getAllSeats() {
        return seatRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<seat> getSeatById(@PathVariable Long id) {
        return seatRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public seat addSeat(@RequestBody seat seat) {
        return seatRepository.save(seat);
    }

    @PutMapping("/{id}")
    public ResponseEntity<seat> updateSeat(
            @PathVariable Long id,
            @RequestBody seat updatedSeat) {

        return seatRepository.findById(id)
                .map(existingSeat -> {
                    existingSeat.setSeatNumber(updatedSeat.getSeatNumber());
                    existingSeat.setSeatType(updatedSeat.getSeatType());
                    existingSeat.setScreen(updatedSeat.getScreen());

                    return ResponseEntity.ok(seatRepository.save(existingSeat));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSeat(@PathVariable Long id) {

        if (!seatRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        seatRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

