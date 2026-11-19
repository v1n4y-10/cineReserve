
package org.example.movies.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class BookingResponse {

    private final Long bookingId;
    private final Long userId;
    private final LocalDateTime bookingTime;
    private final BigDecimal totalAmount;
    private final String status;
    private final List<Long> showSeatIds;

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
        this.showSeatIds = List.copyOf(showSeatIds);
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
