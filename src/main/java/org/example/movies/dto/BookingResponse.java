package org.example.movies.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class BookingResponse {

    private Long bookingId;
    private Long userId;
    private LocalDateTime bookingTime;
    private BigDecimal totalAmount;
    private String status;
    private List<Long> showSeatIds;

    public BookingResponse(
            Long bookingId,
            Long userId,
            LocalDateTime bookingTime,
            BigDecimal totalAmount,
            String status,
            List<Long> showSeatIds) {

        this.bookingId = bookingId;
        this.userId = userId;
        this.bookingTime = bookingTime;
        this.totalAmount = totalAmount;
        this.status = status;
        this.showSeatIds = showSeatIds;
    }
    public Long getBookingId() {
        return bookingId;
    }

    public Long getUserId() {
        return userId;
    }

    public LocalDateTime getBookingTime() {
        return bookingTime;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public List<Long> getShowSeatIds() {
        return showSeatIds;
    }

}