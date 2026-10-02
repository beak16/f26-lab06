package edu.cmu.cs214.booking;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

/** The producer's own suite. It never touches the consumer module. */
class InMemoryBookingServiceTest {

    private final BookingApi api = new InMemoryBookingService();

    @Test
    void freeRoomGivesConfirmedBooking() {
        Booking booking = api.createBooking("R1", 540, 600, null);

        assertEquals(BookingStatus.CONFIRMED, booking.getStatus());
        assertEquals(540, booking.getStartMinute());
    }

    @Test
    void conflictWithoutKeyReturnsNull() {
        api.createBooking("R1", 540, 600, null);

        assertNull(api.createBooking("R1", 570, 630, null));
        assertEquals(1, api.listBookings("R1").size());
    }

    @Test
    void conflictWithKeyGoesOnWaitlist() {
        api.createBooking("R1", 540, 600, null);

        Booking queued = api.createBooking("R1", 570, 630, "party-of-four");

        assertEquals(BookingStatus.WAITLISTED, queued.getStatus());
        assertEquals("party-of-four", queued.getWaitlistKey());
    }

    @Test
    void touchingRangesDoNotConflict() {
        api.createBooking("R1", 540, 600, null);

        Booking next = api.createBooking("R1", 600, 660, null);

        assertEquals(BookingStatus.CONFIRMED, next.getStatus());
    }

    @Test
    void cancelWithNotifyPromotesTheWaitlistedBooking() {
        Booking held = api.createBooking("R1", 540, 600, null);
        Booking queued = api.createBooking("R1", 570, 630, "party-of-four");

        assertTrue(api.cancelBooking(held.getId(), true));

        assertEquals(BookingStatus.CONFIRMED, queued.getStatus());
        List<Booking> schedule = api.listBookings("R1");
        assertEquals(1, schedule.size());
        assertEquals(queued.getId(), schedule.get(0).getId());
    }

    @Test
    void notesAreStoredOnConfirmedBooking() {
        Booking booking = api.createBooking("R1", 540, 600, null, "needs projector");

        assertEquals(BookingStatus.CONFIRMED, booking.getStatus());
        assertEquals("needs projector", booking.getNotes());
    }

    @Test
    void notesAreStoredOnWaitlistedBooking() {
        api.createBooking("R1", 540, 600, null);

        Booking queued = api.createBooking("R1", 570, 630, "party-of-four", "window seat");

        assertEquals(BookingStatus.WAITLISTED, queued.getStatus());
        assertEquals("party-of-four", queued.getWaitlistKey());
        assertEquals("window seat", queued.getNotes());
    }

    @Test
    void notesDoNotChangeConflictWithoutKey() {
        api.createBooking("R1", 540, 600, null);

        assertNull(api.createBooking("R1", 570, 630, null, "please squeeze us in"));
        assertEquals(1, api.listBookings("R1").size());
    }

    @Test
    void fourArgumentCallHasNullNotes() {
        Booking booking = api.createBooking("R1", 540, 600, null);

        assertNull(booking.getNotes());
    }
}
