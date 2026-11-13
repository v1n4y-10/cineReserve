
package org.example.movies.dto;

import java.util.List;

public class BookingRequest {

    private List<Long> showSeatIds;

    public List<Long> getShowSeatIds() {
        return showSeatIds;
    }

    public void setShowSeatIds(List<Long> showSeatIds) {
        this.showSeatIds = showSeatIds;
    }
}
