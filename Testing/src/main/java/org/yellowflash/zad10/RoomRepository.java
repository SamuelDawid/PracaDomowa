package org.yellowflash.zad10;

import java.util.Optional;

public interface RoomRepository {
    Optional<Room> findById(String roomId);
}
