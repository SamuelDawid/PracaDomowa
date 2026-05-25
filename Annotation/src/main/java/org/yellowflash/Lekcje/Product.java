import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Data
@Builder
public class Product {
    private Long id;
    private String name;
    private double price;

    private void m1() {
    }
}
