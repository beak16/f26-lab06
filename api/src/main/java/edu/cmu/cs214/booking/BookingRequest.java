package edu.cmu.cs214.booking;

/**
 * Everything a caller supplies to {@link BookingApi#createBooking(BookingRequest)}.
 *
 * <p>Start with {@link #of(String, long, long)} for the room and the half-open
 * range {@code [startMinute, endMinute)}, then add the optional parts. Each
 * {@code with} method returns a new request; requests are immutable.
 *
 * <p>Nothing is validated here. The API validates when the request is
 * submitted, as its javadoc describes.
 */
public final class BookingRequest {

    private final String roomId;
    private final long startMinute;
    private final long endMinute;
    private final String waitlistKey;
    private final String notes;

    private BookingRequest(String roomId, long startMinute, long endMinute,
                           String waitlistKey, String notes) {
        this.roomId = roomId;
        this.startMinute = startMinute;
        this.endMinute = endMinute;
        this.waitlistKey = waitlistKey;
        this.notes = notes;
    }

    /** A request for the room and range, with no waitlist key and no notes. */
    public static BookingRequest of(String roomId, long startMinute, long endMinute) {
        return new BookingRequest(roomId, startMinute, endMinute, null, null);
    }

    /** This request with the given waitlist key, or null to decline waitlisting. */
    public BookingRequest withWaitlistKey(String waitlistKey) {
        return new BookingRequest(roomId, startMinute, endMinute, waitlistKey, notes);
    }

    /** This request with the given notes, or null for none. */
    public BookingRequest withNotes(String notes) {
        return new BookingRequest(roomId, startMinute, endMinute, waitlistKey, notes);
    }

    public String getRoomId() {
        return roomId;
    }

    public long getStartMinute() {
        return startMinute;
    }

    public long getEndMinute() {
        return endMinute;
    }

    /** The waitlist key, or null if waitlisting is declined. */
    public String getWaitlistKey() {
        return waitlistKey;
    }

    /** The notes, or null if none were given. */
    public String getNotes() {
        return notes;
    }
}
