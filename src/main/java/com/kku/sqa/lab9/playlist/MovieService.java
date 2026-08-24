package com.kku.sqa.lab9.playlist;

import java.time.LocalDate;
import java.util.List;

/**
 * <<interface>> MovieService
 *
 * Sits between {@link com.kku.sqa.lab9.playlist.NowPlaying} (the SUT) and
 * the external {@link MoviePortal}. NowPlaying only ever talks to this
 * interface, which is what makes it possible to substitute a Stub in unit
 * tests instead of the real MoviePortal-backed implementation.
 */
public interface MovieService {

    /**
     * Requests the list of movies currently showing at {@code location} on
     * {@code date} (unfiltered - all cinema types included).
     */
    List<Movie> getShowingMovies(String location, LocalDate date);
}
