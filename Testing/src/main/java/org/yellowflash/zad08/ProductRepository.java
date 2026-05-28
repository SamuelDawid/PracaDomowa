package org.yellowflash.zad08;

import java.util.Optional;

public interface ProductRepository {
    Optional<Product> findById(String productId);
}
