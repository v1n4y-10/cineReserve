
package org.example.movies.controller;

import org.example.movies.dto.BookingRequest;
import org.example.movies.dto.BookingResponse;
import org.example.movies.entity.user;
import org.example.movies.repository.userRepository;
import org.example.movies.service.BookingService;
import org.example.movies.exception.ResourceNotFoundException;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final userRepository userRepository;

    public BookingController(
            BookingService bookingService,
            userRepository userRepository) {

        this.bookingService = bookingService;
        this.userRepository = userRepository;
    }

    // Get logged-in user using JWT email
    private user getCurrentUser(Authentication authentication) {

        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));
    }

    // CREATE BOOKING
    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(
            @Valid @RequestBody BookingRequest request,
            Authentication authentication) {

        user currentUser = getCurrentUser(authentication);

        BookingResponse response = bookingService.createBooking(
                currentUser.getId(),
                request.getShowSeatIds()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // GET OWN BOOKING HISTORY
    @GetMapping("/my-bookings")
    public ResponseEntity<List<BookingResponse>> getUserBookings(
            Authentication authentication) {

        user currentUser = getCurrentUser(authentication);

        List<BookingResponse> response =
                bookingService.getUserBookings(currentUser.getId());

        return ResponseEntity.ok(response);
    }

    // CANCEL OWN BOOKING
    @PutMapping("/{bookingId}/cancel")
    public ResponseEntity<BookingResponse> cancelBooking(
            @PathVariable Long bookingId,
            Authentication authentication) {

        user currentUser = getCurrentUser(authentication);

        // Check whether booking exists and belongs to current user
        if (bookingId == null || bookingId <= 0) {
            throw new ResourceNotFoundException("Booking not found");
        }

        boolean ownsBooking = bookingService
                .userOwnsBooking(bookingId, currentUser.getId());

        if (!ownsBooking) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "You cannot cancel another user's booking");
        }

        BookingResponse response =
                bookingService.cancelBooking(bookingId);

        return ResponseEntity.ok(response);
    }
}
