
package org.example.movies.service;

import org.example.movies.entity.*;
import org.example.movies.repository.*;
import org.example.movies.exception.ResourceNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import org.example.movies.dto.BookingResponse;

@Service
public class BookingService {

    private final bookingRepository bookingRepository;
    private final booking_seatRepository booking_seatRepository;
    private final show_seatRepository show_seatRepository;
    private final userRepository userRepository;

    public BookingService(
            bookingRepository bookingRepository,
            booking_seatRepository booking_seatRepository,
            show_seatRepository show_seatRepository,
            userRepository userRepository) {

        this.bookingRepository = bookingRepository;
        this.booking_seatRepository = booking_seatRepository;
        this.show_seatRepository = show_seatRepository;
        this.userRepository = userRepository;
    }

    // CREATE BOOKING
    @Transactional
    public booking createBooking(Long userId, List<Long> showSeatIds) {

        // Validate user ID
        if (userId == null || userId <= 0) {
            throw new ResourceNotFoundException("User not found");
        }

        // Find user
        user customer = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Validate selected seats
        if (showSeatIds == null || showSeatIds.isEmpty()) {
            throw new RuntimeException("Please select at least one seat");
        }

        // Maximum 10 seats per booking
        if (showSeatIds.size() > 10) {
            throw new RuntimeException("You can book a maximum of 10 seats at once");
        }

        // Check for null or invalid seat IDs
        if (showSeatIds.stream().anyMatch(id -> id == null || id <= 0)) {
            throw new RuntimeException("Invalid seat ID");
        }

        // Prevent duplicate seat selection
        if (new HashSet<>(showSeatIds).size() != showSeatIds.size()) {
            throw new RuntimeException("Duplicate seat IDs are not allowed");
        }

        // Sort IDs to acquire locks in a consistent order
        List<Long> sortedIds = new ArrayList<>(showSeatIds);
        Collections.sort(sortedIds);

        // Lock and retrieve selected seats
        List<show_seat> selectedSeats = new ArrayList<>();

        for (Long id : sortedIds) {

            show_seat selectedSeat = show_seatRepository
                    .findByIdForUpdate(id)
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Show seat not found: " + id));

            // Check availability
            if (!"AVAILABLE".equals(selectedSeat.getStatus())) {
                throw new RuntimeException("Seat is not available: " + id);
            }

            selectedSeats.add(selectedSeat);
        }

        // Ensure all seats belong to the same show
        Long showId = selectedSeats.get(0).getShow().getId();

        for (show_seat selectedSeat : selectedSeats) {

            if (!showId.equals(selectedSeat.getShow().getId())) {
                throw new RuntimeException(
                        "All selected seats must belong to the same show");
            }
        }

        // Calculate total booking amount
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (show_seat selectedSeat : selectedSeats) {

            BigDecimal ticketPrice = selectedSeat.getShow().getTicketPrice();

            if (ticketPrice == null || ticketPrice.compareTo(BigDecimal.ZERO) < 0) {
                throw new RuntimeException("Invalid ticket price");
            }

            totalAmount = totalAmount.add(ticketPrice);
        }

        // Create booking
        booking newBooking = new booking();

        newBooking.setUser(customer);
        newBooking.setBookingTime(LocalDateTime.now());
        newBooking.setTotalAmount(totalAmount);
        newBooking.setStatus("CONFIRMED");

        booking savedBooking = bookingRepository.save(newBooking);

        // Create booking seats and update availability
        List<booking_seat> bookingSeats = new ArrayList<>();

        for (show_seat selectedSeat : selectedSeats) {

            selectedSeat.setStatus("BOOKED");

            booking_seat bookingSeat = new booking_seat();

            bookingSeat.setBooking(savedBooking);
            bookingSeat.setShow_seat(selectedSeat);

            bookingSeats.add(bookingSeat);
        }

        // Save all changes within the same transaction
        show_seatRepository.saveAll(selectedSeats);
        booking_seatRepository.saveAll(bookingSeats);

        List<Long> bookedSeatIds = selectedSeats.stream()
                .map(show_seat::getId)
                .toList();

        return new BookingResponse(
                savedBooking.getId(),
                customer.getId(),
                savedBooking.getBookingTime(),
                savedBooking.getTotalAmount(),
                savedBooking.getStatus(),
                bookedSeatIds
        );
    }

    // GET USER BOOKINGS
    @Transactional(readOnly = true)
    public List<BookingResponse> getUserBookings(Long userId) {

        user customer = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        List<booking> bookings =
                bookingRepository.findByUser_Id(customer.getId());

        return bookings.stream()
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
    }
    @Transactional(readOnly = true)
    public boolean userOwnsBooking(Long bookingId, Long userId) {

        booking existingBooking = bookingRepository.findById(bookingId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Booking not found"));

        return existingBooking.getUser().getId().equals(userId);
    }

    // CANCEL BOOKING
    @Transactional
    public BookingResponse cancelBooking(Long bookingId) {

        if (bookingId == null || bookingId <= 0) {
            throw new ResourceNotFoundException("Booking not found");
        }

        // Lock booking to prevent simultaneous cancellation
        booking existingBooking = bookingRepository.findByIdForUpdate(bookingId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Booking not found"));

        // Only confirmed bookings can be cancelled
        if (!"CONFIRMED".equals(existingBooking.getStatus())) {
            throw new RuntimeException(
                    "Only confirmed bookings can be cancelled");
        }

        // Collect associated show seat IDs
        List<Long> seatIds = new ArrayList<>();

        for (booking_seat bookingSeat : existingBooking.getBookingSeats()) {

            seatIds.add(bookingSeat.getShow_seat().getId());
        }

        if (seatIds.isEmpty()) {
            throw new RuntimeException("Booking has no seats to release");
        }

        // Sort IDs to maintain consistent locking order
        Collections.sort(seatIds);

        // Lock all associated seats
        List<show_seat> selectedSeats = new ArrayList<>();

        for (Long id : seatIds) {

            show_seat selectedSeat = show_seatRepository
                    .findByIdForUpdate(id)
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Show seat not found: " + id));

            // Verify that the seat is currently booked
            if (!"BOOKED".equals(selectedSeat.getStatus())) {
                throw new RuntimeException(
                        "Seat is not currently booked: " + id);
            }

            selectedSeats.add(selectedSeat);
        }

        // Release seats
        for (show_seat selectedSeat : selectedSeats) {
            selectedSeat.setStatus("AVAILABLE");
        }

        show_seatRepository.saveAll(selectedSeats);

        // Update booking status
        existingBooking.setStatus("CANCELLED");

        booking savedBooking = bookingRepository.save(existingBooking);

        return new BookingResponse(
                savedBooking.getId(),
                savedBooking.getUser().getId(),
                savedBooking.getBookingTime(),
                savedBooking.getTotalAmount(),
                savedBooking.getStatus(),
                seatIds
        );
    }

}
