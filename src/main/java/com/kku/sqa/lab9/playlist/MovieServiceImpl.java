package com.kku.sqa.lab9.playlist;

import java.time.LocalDate;
import java.util.List;

/**
 * Real implementation of {@link MovieService} used in production. It simply
 * delegates to the external {@link MoviePortal}. This class is NOT used by
 * the Activity 9.1 unit test - it is only wired up when the whole
 * application runs for real (e.g. from a Spring bean / main method), which
 * is why the SUT (NowPlaying) depends on the MovieService interface rather
 * than on this class directly.
 */
public class MovieServiceImpl implements MovieService {

    private final MoviePortal moviePortal;

    public MovieServiceImpl(MoviePortal moviePortal) {
        this.moviePortal = moviePortal;
    }

    @Override
    public List<Movie> getShowingMovies(String location, LocalDate date) {
        return moviePortal.fetchMovies(location, date);
    }
}
