package com.kku.sqa.lab9.playlist;

import java.time.LocalDate;
import java.util.List;

/**
 * Hand-written Test Stub for {@link MovieService}.
 *
 * This is a classic "Stub" (as opposed to a Mock): it always returns a
 * fixed, pre-programmed list of movies regardless of the input arguments,
 * and it records no interactions/verifications. Its only job is to remove
 * the real dependency on MoviePortal so that NowPlaying's filtering logic
 * can be exercised deterministically in a unit test.
 */
public class StubMovieService implements MovieService {

    @Override
    public List<Movie> getShowingMovies(String location, LocalDate date) {
        return List.of(
                new Movie("The Odyssey", "VIP", location, date),
                new Movie("Spider-Man: Brand New Day", "IMAX Laser", location, date),
                new Movie("The End of Oak Street", "Standard", location, date),
                new Movie("Zootopia 3", "VIP", location, date),
                new Movie("Avatar: Fire and Ash", "4DX", location, date)
        );
    }
}
