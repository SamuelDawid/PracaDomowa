package org.yellowflash.zad08;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.yellowflash.zad07.exercise.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ShoppingCartService - testy mockowania")
class ShoppingCartServiceTest {
    @Mock
    ProductRepository productRepository;
    @Mock
    DiscountService discountService;
    @InjectMocks
    ShoppingCartService service;
    /// Something to work with
    Product tv = new Product("1","tv",199.99);
    Product cd = new Product("2","cd",200);
    Product P1 = new Product("P1","desk",100);
    Product P2 = new Product("P2","book",50);

    @Nested class CalculateTotalTests{
        @Test
        @DisplayName("Zwroc total dla jednego przedmiotu")
        void shouldCalculateTotalForSingleItem(){
            //Arrange
            when(productRepository.findById("1")).thenReturn(Optional.of(tv));
            when(discountService.getDiscountForCustomer("123")).thenReturn(0.0);
            List<CartItem> cartWithOneItem = new ArrayList<>(
                    List.of(new CartItem("1",1)));
            //act
            double result = service.calculateTotal("123",cartWithOneItem);
            //assert
            assertThat(result).isEqualTo(199.99);
            verify(productRepository).findById("1");
            verify(discountService).getDiscountForCustomer("123");

        }
        @Test
        @DisplayName("naloz poprawnie obnizke na towar")
        void shouldApplyDiscountCorrectly(){
            //Arrange
            when(productRepository.findById("2")).thenReturn(Optional.of(cd));
            when(discountService.getDiscountForCustomer("123")).thenReturn(0.1);
            List<CartItem> cartWithOneItemButTwoQuant = new ArrayList<>(
                    List.of(new CartItem("2",2)));
            //act
            double result = service.calculateTotal("123",cartWithOneItemButTwoQuant);
            //assert
            assertThat(result).isCloseTo(360,within(0.01));
        }
        @Test
        @DisplayName("Zwroc sume dla kilku produktow bez rabatu")
        void shouldCalculateTotalForMultipleProducts(){
            //Arrange
            when(discountService.getDiscountForCustomer("123")).thenReturn(0.0);
            when(productRepository.findById("P1")).thenReturn(Optional.of(P1));
            when(productRepository.findById("P2")).thenReturn(Optional.of(P2));
            List<CartItem> cartWithTwoItems = new ArrayList<>(
                    List.of(new CartItem("P2",3),
                            new CartItem("P1",1)));

            //act
            double result = service.calculateTotal("123",cartWithTwoItems);
            //assert
            assertThat(result).isEqualTo(250);
        }
        @Test
        @DisplayName("Powinnien rzucic IllegalArgumentException jezeli karta jest pusta")
        void shouldThrowExceptionForEmptyCart(){
            //Arrage
            List<CartItem> emptyCart = new ArrayList<>();
            // act + assert
           assertThrows(IllegalArgumentException.class, () -> service.calculateTotal("123",emptyCart));
            verifyNoInteractions(productRepository);
            verifyNoInteractions(discountService);

        }
        @Test
        @DisplayName("Powinien rzucic Excpetion jezeli product nie zostal znaleziony")
        void shouldThrowExceptionWhenProductNotFound(){
            //Arrage
            when(productRepository.findById("P999")).thenReturn(Optional.empty());
            List<CartItem> cart = List.of(new CartItem("P999", 1));

            // ACT + ASSERT
            ProductNotFoundException ex = assertThrows(
                    ProductNotFoundException.class,
                    () -> service.calculateTotal("123", cart)
            );
            assertThat(ex.getMessage()).isEqualTo("Produkt nie znaleziony: P999");
        }
    }

}