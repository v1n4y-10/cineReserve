
package org.example.movies.entity;

import jakarta.persistence.*;

@Entity
@Table(
        name = "booking_seat",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"booking_id", "show_seat_id"})
        }
)
public class booking_seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "booking_id", nullable = false)
    private booking booking;

    @ManyToOne
    @JoinColumn(name = "show_seat_id", nullable = false)
    private show_seat show_seat;

    public booking_seat() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public booking getBooking() {
        return booking;
    }

    public void setBooking(booking booking) {
        this.booking = booking;
    }

    public show_seat getShow_seat() {
        return show_seat;
    }

    public void setShow_seat(show_seat show_seat) {
        this.show_seat = show_seat;
    }
}
