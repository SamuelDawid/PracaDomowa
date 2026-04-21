import javax.sound.midi.Soundbank;
import java.util.List;

public class InvoiceProcessor {

    public void generateReport(List<Invoice> invoices) {
        System.out.println("=== Raport faktur ===");
        for (Invoice invoice : invoices) {
            String line = formatInvoice(invoice);
            System.out.println(line);
        }
    }

    private String formatInvoice(Invoice invoice) {
        String customerInfo = getCustomerInfo(invoice.getCustomer());
        double total = calculateTotal(invoice.getItems());
        return invoice.getId() + " | " + customerInfo + " | Razem: " + total + " zł";
    }

    private String getCustomerInfo(Customer customer) {
        try {
            String address = customer.getAddress();
            // NPE gdy address jest null!
            return customer.getName() + ", " + address.toUpperCase();
        } catch (RuntimeException e) {
            System.out.println("No adress for " + customer.name);
        }
        return null;
    }

    private double calculateTotal(List<InvoiceItem> items) {
        double total = 0;
        for (InvoiceItem item : items) {
            total += item.getQuantity() * item.getUnitPrice();
        }
        return total;
    }

    public static void main(String[] args) {
        InvoiceProcessor processor = new InvoiceProcessor();

        List<Invoice> invoices = List.of(
                new Invoice("FV-001",
                        new Customer("Jan Kowalski", "ul. Kwiatowa 5, Warszawa"),
                        List.of(new InvoiceItem("Laptop", 1, 3500.0),
                                new InvoiceItem("Myszka", 2, 89.99))),
                new Invoice("FV-002",
                        new Customer("Anna Nowak", null),  // Anna nie ma adresu!
                        List.of(new InvoiceItem("Monitor", 1, 1200.0))),
                new Invoice("FV-003",
                        new Customer("Piotr Wiśniewski", "ul. Leśna 12, Kraków"),
                        List.of(new InvoiceItem("Klawiatura", 1, 250.0),
                                new InvoiceItem("Słuchawki", 1, 350.0)))
        );

        processor.generateReport(invoices);
    }

    static class Invoice {
        private String id;
        private Customer customer;
        private List<InvoiceItem> items;

        Invoice(String id, Customer customer, List<InvoiceItem> items) {
            this.id = id;
            this.customer = customer;
            this.items = items;
        }

        String getId() { return id; }
        Customer getCustomer() { return customer; }
        List<InvoiceItem> getItems() { return items; }
    }

    static class Customer {
        private String name;
        private String address;

        Customer(String name, String address) {
            this.name = name;
            this.address = address;
        }

        String getName() { return name; }
        String getAddress() { return address; }
    }

    static class InvoiceItem {
        private String productName;
        private int quantity;
        private double unitPrice;

        InvoiceItem(String productName, int quantity, double unitPrice) {
            this.productName = productName;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
        }

        String getProductName() { return productName; }
        int getQuantity() { return quantity; }
        double getUnitPrice() { return unitPrice; }
    }
    /*
    1. Na jakiej linii debugger się zatrzymał? Która zmienna jest `null`?
    Linia 44
2. Dla której faktury (jaki `id`) wystąpił problem?
FV-002
3. Jak naprawić bug? (Podpowiedź: sprawdź `null` przed wywołaniem `toUpperCase()`)
 try {
            String address = customer.getAddress();
            // NPE gdy address jest null!
            return customer.getName() + ", " + address.toUpperCase();
        } catch (RuntimeException e) {
            sout("No adress for " + customer.name);
        }
4. Jaka jest różnica między Exception Breakpoint z opcją "Caught" a "Uncaught"? Kiedy który wybrać?
Uncaught - zatrzymuje sie tylko gdy wyjatek nie jest obsluzony i powoduje crash programu, uzywasz gdy szukasz nieoczekiwanych bledow.
Cayght - zatrzymuje sie zawsze gdy wyjatek jest rzucony, nawet jesli jest obsluzony przez try-catch, uzywasz gdy chcesz zobaczyc skad pochodzi wyjatek
5. Dlaczego nawigacja po Call Stack (Frames) pomogła w zrozumieniu problemu?
Bo Call Stack pokazuje cala sciezke jak program doszedl do danego miejsca i ktora metoda wywolala ktora, w jakiej kolejnosci i z jakimi wartosciami.
     */
}