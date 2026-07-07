package org.yellowflash.zad08;

import java.util.List;

public class ShoppingCartService {

    private final ProductRepository productRepository;
    private final DiscountService discountService;

    public ShoppingCartService(ProductRepository productRepository,
                               DiscountService discountService) {
        this.productRepository = productRepository;
        this.discountService = discountService;
    }

    public double calculateTotal(String customerId, List<CartItem> items) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Koszyk nie może być pusty");
        }

        double subtotal = 0.0;
        for (CartItem item : items) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new ProductNotFoundException(item.getProductId()));
            subtotal += product.getPrice() * item.getQuantity();
        }

        double discount = discountService.getDiscountForCustomer(customerId);
        return subtotal * (1.0 - discount);
    }
    public CartSummary getCartSummary(String customerId,List<CartItem> items){
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Koszyk nie może być pusty");
        }
        double subtotal = 0.0;
        int itemsCount = 0;
        for (CartItem item : items) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new ProductNotFoundException(item.getProductId()));
            subtotal += product.getPrice() * item.getQuantity();
            itemsCount+= item.getQuantity();
        }
        double discount = discountService.getDiscountForCustomer(customerId);

        return new CartSummary(
                subtotal,discount,
                calculateTotal(customerId,items),
                itemsCount
        );

    }
}
