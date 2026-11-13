package org.example.movies.entity;
import jakarta.persistence.*;

@Entity
@Table(
        name = "show_seat",
        uniqueConstraints = {
                @UniqueConstraint(columnNames =  {"show_id", "seat_id"})
        }
)
public class show_seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "show_id", nullable = false)
    private show show;

    @ManyToOne
    @JoinColumn(name = "seat_id", nullable = false)
    private seat seat;

    private String status;

    public show_seat() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public show getShow() {
        return show;
    }

    public void setShow(show show) {
        this.show = show;
    }

    public seat getSeat() {
        return seat;
    }

    public void setSeat(seat seat) {
        this.seat = seat;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
