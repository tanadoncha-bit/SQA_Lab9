package com.kku.sqa.lab9.playlist;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class NowPlaying (System Under Test for Activity 9.1).
 *
 * Dependency analysis:
 *   NowPlaying --> MovieService (interface) --> MoviePortal (external)
 *
 * NowPlaying's own responsibility is purely the filtering logic: given the
 * unfiltered list of movies that MovieService returns, narrow it down to
 * only the entries matching a requested cinema type (e.g. "VIP",
 * "IMAX Laser"). Because the real MovieService ultimately depends on the
 * external MoviePortal, a Stub implementation of MovieService is used in
 * the unit test so NowPlaying's filtering logic can be verified in
 * isolation, without any real network call.
 */
public class NowPlaying {

    private final MovieService movieService;

    public NowPlaying(MovieService movieService) {
        this.movieService = movieService;
    }

    /**
     * Returns only the movies showing at {@code location} on {@code date}
     * whose cinema type matches {@code cinemaType} (case-insensitive).
     */
    public List<Movie> getMoviesByCinemaType(String location, LocalDate date, String cinemaType) {
        List<Movie> allShowing = movieService.getShowingMovies(location, date);
        return allShowing.stream()
                .filter(movie -> movie.getCinemaType().equalsIgnoreCase(cinemaType))
                .collect(Collectors.toList());
    }
}
