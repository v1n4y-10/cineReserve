package org.example.movies.service;

import org.example.movies.entity.*;
import org.example.movies.repository.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

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
    @Transactional
    public booking createBooking(Long userId, List<Long> showSeatIds) {
        // Check user
        user customer = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        // Check selected seats
        if (showSeatIds == null || showSeatIds.isEmpty()) {
            throw new RuntimeException("Please select at least one seat");
        }
        //prevention of duplicate seat selection
        if (new HashSet<>(showSeatIds).size() != showSeatIds.size()) {
            throw new RuntimeException("Duplicate seat IDs are not allowed");
        }
        // Lock seats in a consistent order
        List<Long> sortedIds = new ArrayList<>(showSeatIds);
        Collections.sort(sortedIds);
        //locking and retreiving seats
        List<show_seat> selectedSeats = new ArrayList<>();

        for (Long id : sortedIds) {

            show_seat selectedSeat = show_seatRepository
                    .findByIdForUpdate(id)
                    .orElseThrow(() -> new RuntimeException("Show seat not found"));

            if (!"AVAILABLE".equals(selectedSeat.getStatus())) {
                throw new RuntimeException(
                        "Seat is not available: " + id
                );
            }

            selectedSeats.add(selectedSeat);
        }
        // Make sure all seats belong to the same show
        Long showId = selectedSeats.get(0).getShow().getId();

        for (show_seat selectedSeat : selectedSeats) {
            if (!showId.equals(selectedSeat.getShow().getId())) {
                throw new RuntimeException(
                        "All selected seats must belong to the same show"
                );
            }
        }
        // Calculate total amount
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (show_seat selectedSeat : selectedSeats) {
            totalAmount = totalAmount.add(
                    selectedSeat.getShow().getTicketPrice()
            );
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
        //save everything and return booking
        show_seatRepository.saveAll(selectedSeats);
        booking_seatRepository.saveAll(bookingSeats);

        return savedBooking;
    }
    // Get user bookings
    public List<booking> getUserBookings(Long userId) {

        user customer = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return bookingRepository.findByUser_Id(customer.getId());
    }

    @Transactional
    public booking cancelBooking(Long bookingId) {

        // Find booking
        booking existingBooking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // Check booking status
        if (!"CONFIRMED".equals(existingBooking.getStatus())) {
            throw new RuntimeException("Only confirmed bookings can be cancelled");
        }

        // Release all booked seats
        for (booking_seat bookingSeat : existingBooking.getBookingSeats()) {

            show_seat selectedSeat = bookingSeat.getShow_seat();

            selectedSeat.setStatus("AVAILABLE");

            show_seatRepository.save(selectedSeat);
        }

        // Update booking status
        existingBooking.setStatus("CANCELLED");

        return bookingRepository.save(existingBooking);
    }

}
