package com.kku.sqa.lab9.playlist;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Activity 9.1 - unit test for NowPlaying using a Stub in place of the
 * real MovieService/MoviePortal dependency chain.
 */
class NowPlayingTest {

    private NowPlaying nowPlaying;

    @BeforeEach
    void setUp() {
        // Test Double: a Stub replaces the real MovieService so this test
        // never touches the external MoviePortal.
        MovieService stubMovieService = new StubMovieService();
        nowPlaying = new NowPlaying(stubMovieService);
    }

    @Test
    @DisplayName("Filtering by VIP should return only VIP-cinema movies")
    void getMoviesByCinemaType_vipOnly_returnsOnlyVipMovies() {
        List<Movie> result = nowPlaying.getMoviesByCinemaType(
                "Central Khon Kaen", LocalDate.of(2026, 9, 1), "VIP");

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(m -> m.getCinemaType().equalsIgnoreCase("VIP")));
        assertTrue(result.stream().anyMatch(m -> m.getTitle().equals("The Odyssey")));
        assertTrue(result.stream().anyMatch(m -> m.getTitle().equals("Zootopia 3")));
    }

    @Test
    @DisplayName("Filtering by a cinema type that matches is case-insensitive")
    void getMoviesByCinemaType_isCaseInsensitive() {
        List<Movie> result = nowPlaying.getMoviesByCinemaType(
                "Central Khon Kaen", LocalDate.of(2026, 9, 1), "vip");

        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Filtering by a cinema type with no matches returns an empty list")
    void getMoviesByCinemaType_noMatches_returnsEmptyList() {
        List<Movie> result = nowPlaying.getMoviesByCinemaType(
                "Central Khon Kaen", LocalDate.of(2026, 9, 1), "Dolby Cinema");

        assertTrue(result.isEmpty());
    }
}
