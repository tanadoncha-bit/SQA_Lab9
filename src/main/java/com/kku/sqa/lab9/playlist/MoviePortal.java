package com.kku.sqa.lab9.playlist;

import java.time.LocalDate;
import java.util.List;

/**
 * <<external>> MoviePortal
 *
 * Represents the third-party / external system that NowPlaying ultimately
 * depends on. In production this would call out over the network (REST,
 * SOAP, etc.) to a real movie-listing provider, which is exactly why it
 * needs to be replaced by a Test Double when testing NowPlaying in
 * isolation.
 */
public interface MoviePortal {

    /**
     * Returns every movie showing at the given location on the given date,
     * across all cinema/theater types.
     */
    List<Movie> fetchMovies(String location, LocalDate date);
}
