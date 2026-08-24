package sqa.lab.service;

/**
 * CP353201 Software Quality Assurance (1/2569)
 * Lab#9 - Test Double, Activity 9.2a
 *
 * SUT: SeatReservation.checkSeatAvailability(String)
 * Dependency mocked: SeatDAO.fetchAvailableSeats() - the "available seat
 * numbers" response required by instruction 3(a). In production this
 * method opens a real JDBC connection (DriverManager.getConnection(...)),
 * which is exactly the kind of external dependency that must be replaced
 * by a Mockito mock so SeatReservation's own logic can be tested without
 * a real database.
 */

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SeatReservationTest {

    @Mock
    private SeatDAO seatDAO;

    @Test
    @DisplayName("checkSeatAvailability returns true when the mocked DAO reports the seat as available")
    void checkSeatAvailability_seatIsInAvailableList_returnsTrue() throws SQLException {
        // Mock the response of the available-seat-numbers dependency
        List<String> availableSeats = Arrays.asList("A1", "A2", "B5", "C3");
        when(seatDAO.fetchAvailableSeats()).thenReturn(availableSeats);

        SeatReservation seatReservation = new SeatReservation(seatDAO);

        boolean result = seatReservation.checkSeatAvailability("B5");

        assertTrue(result);
        verify(seatDAO, times(1)).fetchAvailableSeats();
    }

    @Test
    @DisplayName("checkSeatAvailability returns false when the requested seat is not in the mocked available list")
    void checkSeatAvailability_seatNotInAvailableList_returnsFalse() throws SQLException {
        when(seatDAO.fetchAvailableSeats()).thenReturn(Arrays.asList("A1", "A2"));

        SeatReservation seatReservation = new SeatReservation(seatDAO);

        boolean result = seatReservation.checkSeatAvailability("Z9");

        assertFalse(result);
    }

    @Test
    @DisplayName("checkSeatAvailability returns false when the mocked DAO reports no available seats at all")
    void checkSeatAvailability_noSeatsAvailable_returnsFalse() throws SQLException {
        when(seatDAO.fetchAvailableSeats()).thenReturn(Collections.emptyList());

        SeatReservation seatReservation = new SeatReservation(seatDAO);

        assertFalse(seatReservation.checkSeatAvailability("A1"));
    }
}
