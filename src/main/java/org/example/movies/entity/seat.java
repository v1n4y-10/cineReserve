
package org.example.movies.entity;

import jakarta.persistence.*;

@Entity
@Table(
        name = "seat",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"screen_id", "seatNumber"})
        }
)
public class seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String seatNumber;
    private String seatType;

    @ManyToOne
    @JoinColumn(name = "screen_id", nullable = false)
    private screen screen;

    public seat() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    public String getSeatType() {
        return seatType;
    }

    public void setSeatType(String seatType) {
        this.seatType = seatType;
    }

    public screen getScreen() {
        return screen;
    }

    public void setScreen(screen screen) {
        this.screen = screen;
    }
}
