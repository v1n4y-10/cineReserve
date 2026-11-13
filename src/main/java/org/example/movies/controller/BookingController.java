package org.example.movies.controller;

import org.example.movies.dto.BookingRequest;
import org.example.movies.dto.BookingResponse;
import org.example.movies.entity.booking;
import org.example.movies.entity.user;
import org.example.movies.repository.userRepository;
import org.example.movies.repository.bookingRepository;
import org.example.movies.service.BookingService;

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
    private final bookingRepository bookingRepository;

    public BookingController(
            BookingService bookingService,
            userRepository userRepository,
            bookingRepository bookingRepository) {

        this.bookingService = bookingService;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
    }

    // Get Logged-in User
    private user getCurrentUser(Authentication authentication) {

        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // Create Booking
    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(
            @RequestBody BookingRequest request,
            Authentication authentication) {

        user currentUser = getCurrentUser(authentication);

        booking newBooking = bookingService.createBooking(
                currentUser.getId(),
                request.getShowSeatIds()
        );

        BookingResponse response = new BookingResponse(
                newBooking.getId(),
                currentUser.getId(),
                newBooking.getBookingTime(),
                newBooking.getTotalAmount(),
                newBooking.getStatus(),
                request.getShowSeatIds()
        );

        return ResponseEntity.ok(response);
    }

    // Get Own Booking History
    @GetMapping("/my-bookings")
    public ResponseEntity<List<BookingResponse>> getUserBookings(
            Authentication authentication) {

        user currentUser = getCurrentUser(authentication);

        List<booking> bookings =
                bookingService.getUserBookings(currentUser.getId());

        List<BookingResponse> response = bookings.stream()
                .map(b -> new BookingResponse(
                        b.getId(),
                        b.getUser().getId(),
                        b.getBookingTime(),
                        b.getTotalAmount(),
                        b.getStatus(),
                        b.getBookingSeats().stream()
                                .map(bs -> bs.getShow_seat().getId())
                                .toList()
                ))
                .toList();

        return ResponseEntity.ok(response);
    }

    // Cancel Own Booking
    @PutMapping("/{bookingId}/cancel")
    public ResponseEntity<?> cancelBooking(
            @PathVariable Long bookingId,
            Authentication authentication) {

        user currentUser = getCurrentUser(authentication);

        booking existingBooking = bookingRepository.findById(bookingId)
                .orElse(null);

        if (existingBooking == null) {
            return ResponseEntity.notFound().build();
        }

        if (!existingBooking.getUser().getId().equals(currentUser.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You cannot cancel another user's booking");
        }

        booking cancelledBooking =
                bookingService.cancelBooking(bookingId);

        BookingResponse response = new BookingResponse(
                cancelledBooking.getId(),
                cancelledBooking.getUser().getId(),
                cancelledBooking.getBookingTime(),
                cancelledBooking.getTotalAmount(),
                cancelledBooking.getStatus(),
                cancelledBooking.getBookingSeats().stream()
                        .map(bs -> bs.getShow_seat().getId())
                        .toList()
        );

        return ResponseEntity.ok(response);
    }
}
