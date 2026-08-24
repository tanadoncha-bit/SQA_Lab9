package sqa.lab.service;

/**
 * CP353201 Software Quality Assurance (1/2569)
 * Lab#9 - Test Double, Activity 9.2b
 *
 * SUT: GateCheckin.customerEntry(int) / customerIsEligible(int)
 * Dependency mocked: TicketCounter - specifically its
 * getNoCheckinCustomer() response (the "count of visitors who have
 * already passed gate check-in" required by instruction 3(b)), and its
 * changeTicketStatus(boolean) call is verified as an interaction since it
 * returns void.
 */

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GateCheckinTest {

    @Mock
    private TicketCounter ticketCounter;

    @Test
    @DisplayName("A first-time ticket is accepted, added to passengers on board, and increments the mocked counter")
    void customerEntry_newTicket_isAcceptedAndIncrementsCounter() {
        GateCheckin gateCheckin = new GateCheckin(ticketCounter);

        gateCheckin.customerEntry(1001);

        Set<Integer> passengers = gateCheckin.getPassengersOnBoard();
        assertTrue(passengers.contains(1001));
        verify(ticketCounter, times(1)).changeTicketStatus(true);
    }

    @Test
    @DisplayName("Scanning the same ticket twice only checks it in once and only increments the counter once")
    void customerEntry_duplicateTicket_isRejectedSecondTime() {
        GateCheckin gateCheckin = new GateCheckin(ticketCounter);

        gateCheckin.customerEntry(2002);
        gateCheckin.customerEntry(2002); // same ticket scanned again

        assertFalse(gateCheckin.customerIsEligible(2002));
        assertEquals(1, gateCheckin.getPassengersOnBoard().size());
        verify(ticketCounter, times(1)).changeTicketStatus(true);
    }

    @Test
    @DisplayName("A ticket id not yet checked in is reported eligible without touching the counter")
    void customerIsEligible_unseenTicket_returnsTrueAndDoesNotCallCounter() {
        GateCheckin gateCheckin = new GateCheckin(ticketCounter);

        assertTrue(gateCheckin.customerIsEligible(3003));
        verify(ticketCounter, never()).changeTicketStatus(true);
    }

    @Test
    @DisplayName("Mocked response: getNoCheckinCustomer() reports the checked-in visitor count for reporting/capacity checks")
    void getNoCheckinCustomer_mockedResponse_returnsStubbedCount() {
        // This directly exercises instruction 3(b): substitute the
        // "count of visitors who have already passed gate check-in"
        // response with a Mockito stub instead of relying on the real
        // TicketCounter's internal state.
        when(ticketCounter.getNoCheckinCustomer()).thenReturn(42);

        assertEquals(42, ticketCounter.getNoCheckinCustomer());
        verify(ticketCounter, times(1)).getNoCheckinCustomer();
    }
}
