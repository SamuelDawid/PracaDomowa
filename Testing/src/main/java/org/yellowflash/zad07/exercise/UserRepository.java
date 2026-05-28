package org.yellowflash.zad07.exercise;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findById(Long id);
}
