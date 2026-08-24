package com.kku.sqa.lab9.playlist;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Simple data model representing one showtime entry returned by the
 * external MoviePortal system.
 */
public class Movie {

    private final String title;
    private final String cinemaType;   // e.g. "VIP", "IMAX Laser", "Standard"
    private final String location;
    private final LocalDate showDate;

    public Movie(String title, String cinemaType, String location, LocalDate showDate) {
        this.title = title;
        this.cinemaType = cinemaType;
        this.location = location;
        this.showDate = showDate;
    }

    public String getTitle() {
        return title;
    }

    public String getCinemaType() {
        return cinemaType;
    }

    public String getLocation() {
        return location;
    }

    public LocalDate getShowDate() {
        return showDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Movie)) return false;
        Movie movie = (Movie) o;
        return Objects.equals(title, movie.title)
                && Objects.equals(cinemaType, movie.cinemaType)
                && Objects.equals(location, movie.location)
                && Objects.equals(showDate, movie.showDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, cinemaType, location, showDate);
    }

    @Override
    public String toString() {
        return "Movie{title='" + title + "', cinemaType='" + cinemaType
                + "', location='" + location + "', showDate=" + showDate + "}";
    }
}
