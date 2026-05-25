import lombok.extern.slf4j.Slf4j;

//@Slf4j
public class LombokMain {
    public static void main(String[] args) {
//        Product p = new Product();
        Product p1 = new Product(1L, "abc", 2.56);

        Product p2 = Product.builder()
                .id(1L)
                .price(123.231)
                .build();


    }
}
