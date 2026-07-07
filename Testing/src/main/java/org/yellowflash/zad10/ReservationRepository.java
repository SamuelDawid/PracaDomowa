package org.yellowflash.zad10;

public interface ReservationRepository {
    Reservation save(Reservation reservation);
    boolean existsByRoomIdAndTimeSlot(String roomId, TimeSlot timeSlot);
}