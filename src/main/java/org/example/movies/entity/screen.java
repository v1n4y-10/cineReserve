
package org.example.movies.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "screen")
public class screen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theater_id", nullable = false)
    private theater theater;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private Integer totalSeats;

    @Column(length = 50)
    private String screenType;

    public screen() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(Integer totalSeats) {
        this.totalSeats = totalSeats;
    }

    public String getScreenType() {
        return screenType;
    }

    public void setScreenType(String screenType) {
        this.screenType = screenType;
    }

    public theater getTheater() {
        return theater;
    }

    public void setTheater(theater theater) {
        this.theater = theater;
    }
}
