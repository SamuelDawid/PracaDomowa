package BillingServices;

import Excepcions.NotAllInvoicesShareSameCurrency;
import Excepcions.ProcessRefundException;
import Excepcions.SplitNotAvailableException;
import GlobalValues.CorrectionInvoiceType;
import org.junit.Test;
import records.CorrectionInvoice;
import records.Invoice;
import records.Money;
import records.SplitPaymentBreakdown;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.*;

public class InvoiceCalculatorTest {
    // TEST INVOICES //
    Invoice inv1 = new Invoice("INV/2024/10/001", "Jan Kowalski",
            new Money(new BigDecimal("150.00"), "PLN"),
            new BigDecimal("0.23"),
            ZonedDateTime.now(), LocalDate.now(),false);

    Invoice inv2 = new Invoice("INV/2024/01/002", "Anna Nowak",
            new Money(new BigDecimal("33.33"), "PLN"),
            new BigDecimal("0.23"),
            ZonedDateTime.now(), LocalDate.now(),false);

    Invoice inv3 = new Invoice("INV/2024/01/003", "Firma XYZ",
            new Money(new BigDecimal("0.00"), "PLN"),
            new BigDecimal("0.08"),
            ZonedDateTime.now(), LocalDate.now(),false);

    Invoice inv4 = new Invoice("INV/2024/01/004", "Egzotyczny Klient",
            new Money(new BigDecimal("200.50"), "YAP"),
            new BigDecimal("0.05"),
            ZonedDateTime.now(), LocalDate.now(),false);
    private static Money money(String amount, String currency) {
        return new Money(new BigDecimal(amount), currency);
    }

    private static Invoice invoice(String number, String client, ZonedDateTime issuedAt,
                                   String netAmount, String currency, boolean split) {
        return new Invoice(
                number,
                client,
                money(netAmount, currency),
                new BigDecimal("0.23"),           // default VAT for tests; override when a test cares
                issuedAt,
                issuedAt.toLocalDate().plusDays(14), // dueDate 2 weeks after issue — arbitrary, safe default
                split
        );
    }

    private static CorrectionInvoice correction(String adjustmentAmount, String currency,Enum type) {
        return new CorrectionInvoice(
                "COR-" + adjustmentAmount,        // unique enough for a test
                "INV-ORIGINAL",
                money(adjustmentAmount, currency),
                LocalDate.of(2026, 1, 20),
                "test correction",type);
    }
    @org.junit.jupiter.api.Test
    void grossAmount() {
        BigDecimal net = new BigDecimal("100.00");
        BigDecimal vat = new BigDecimal("0.23");
        Invoice test =new Invoice("INV/001", "Test Client", new Money(net,"PLN"), vat, ZonedDateTime.now(), LocalDate.now(),false);
        Money result = new Money(new BigDecimal("123.00"),"PLN");
        assertEquals(result,InvoiceCalculator.grossAmount(test));
    }
    @org.junit.jupiter.api.Test
    void grossAmountZero() {
        BigDecimal net = new BigDecimal("0.00");
        BigDecimal vat = new BigDecimal("0.23");
        Invoice test =new Invoice("INV/001", "Test Client", new Money(net,"PLN"), vat, ZonedDateTime.now(), LocalDate.now(),false);
        Money result = new Money(new BigDecimal("0.00"),"PLN");
        assertEquals(result,InvoiceCalculator.grossAmount(test));
    }
    @org.junit.jupiter.api.Test
    void grossAmountHalfEven() {

//        BigDecimal net = new BigDecimal("33.33");
//        BigDecimal vat = new BigDecimal("0.23");
        BigDecimal net = new BigDecimal("10");
        BigDecimal vat = new BigDecimal("0.115");
        Invoice test =new Invoice("INV/001", "Test Client", new Money(net,"PLN"), vat, ZonedDateTime.now(), LocalDate.now(),false);
        //Money result = new Money(new BigDecimal("41.00"),"PLN"); // returns 41.
        Money result = new Money(new BigDecimal("11.15"),"PLN");
        assertEquals(result, InvoiceCalculator.grossAmount(test));
    }
    @org.junit.jupiter.api.Test
    void vatAmount() {
        BigDecimal net = new BigDecimal("10");
        BigDecimal vat = new BigDecimal("0.23");
        Invoice test =new Invoice("INV/001", "Test Client", new Money(net,"PLN"), vat, ZonedDateTime.now(), LocalDate.now(),false);
        Money result = new Money(new BigDecimal("0.23"),"PLN");

        assertEquals(result,InvoiceCalculator.vatAmount(test));
    }
    @org.junit.jupiter.api.Test
    void totalNet(){
        Money result = new Money(new BigDecimal("183.33"),"PLN");
        assertEquals(result,InvoiceCalculator.totalNet(inv1,inv2,inv3));
    }
    @org.junit.jupiter.api.Test
    void shouldThrowNotAllInvoicesShareSameCurrency() {
        assertThrows(NotAllInvoicesShareSameCurrency.class,() ->{
            InvoiceCalculator.totalNet(inv1,inv2,inv3,inv4);
        });
    }
    @org.junit.jupiter.api.Test
    public void split_invoiceAllowsSplitPayment_returnsBreakdownWithNetAndVat() {
        ZonedDateTime issued = ZonedDateTime.of(2026, 1, 15, 10, 0, 0, 0, ZoneId.of("UTC"));
        Invoice inv = invoice("INV-100", "Split Co", issued, "1000.00", "PLN", true);

        SplitPaymentBreakdown result = InvoiceCalculator.split(inv);

        assertNotNull(result);
        assertEquals(inv.netAmount().amount(), result.mainAccount().amount()); // adjust accessor name if different
        assertEquals(InvoiceCalculator.vatAmount(inv), result.vatAccount());
    }

    @Test(expected = SplitNotAvailableException.class)
    public void split_invoiceDoesNotAllowSplitPayment_throwsSplitNotAvailable() {
        ZonedDateTime issued = ZonedDateTime.of(2026, 1, 15, 10, 0, 0, 0, ZoneId.of("UTC"));
        Invoice inv = invoice("INV-101", "No Split", issued, "500.00", "PLN", false);

        InvoiceCalculator.split(inv); // should throw
    }

    @Test(expected = NullPointerException.class)
    public void split_nullInvoice_throwsNpe() {
        InvoiceCalculator.split(null);
    }
    @org.junit.jupiter.api.Test
    public void effectiveGross_emptyCorrections_returnsOriginalGross() {
        ZonedDateTime issued = ZonedDateTime.of(2026, 1, 15, 10, 0, 0, 0, ZoneId.of("UTC"));
        Invoice original = invoice("INV-200", "Base Co", issued, "1000.00", "EUR", false);

        Money result = InvoiceCalculator.effectiveGross(original, Collections.emptyList());

        // Expect gross to equal grossAmount(original) unchanged
        assertEquals(InvoiceCalculator.grossAmount(original).amount(), result.amount());
        assertEquals("EUR", result.currency());
    }

    @org.junit.jupiter.api.Test
    public void effectiveGross_singleCorrection_addsAdjustmentToGross() {
        ZonedDateTime issued = ZonedDateTime.of(2026, 1, 15, 10, 0, 0, 0, ZoneId.of("UTC"));
        Invoice original = invoice("INV-201", "Base", issued, "1000.00", "EUR", false);
        CorrectionInvoice c1 = correction("50.00", "EUR", CorrectionInvoiceType.SURCHARGE); // adjust constructor if needed

        Money result = InvoiceCalculator.effectiveGross(original, Arrays.asList(c1));

        BigDecimal expected = InvoiceCalculator.grossAmount(original).amount().add(new BigDecimal("50.00"));
        assertEquals(0, expected.compareTo(result.amount())); // compareTo ignores trailing-zero scale
    }

    @org.junit.jupiter.api.Test
    public void effectiveGross_multipleCorrections_sumsAllAdjustments() {
        ZonedDateTime issued = ZonedDateTime.of(2026, 1, 15, 10, 0, 0, 0, ZoneId.of("UTC"));
        Invoice original = invoice("INV-202", "Base", issued, "1000.00", "EUR", false);
        CorrectionInvoice c1 = correction("10.00",  "EUR",CorrectionInvoiceType.SURCHARGE);
        CorrectionInvoice c2 = correction("20.00",  "EUR",CorrectionInvoiceType.SURCHARGE);
        CorrectionInvoice c3 = correction("30.50",  "EUR",CorrectionInvoiceType.SURCHARGE);

        Money result = InvoiceCalculator.effectiveGross(original, Arrays.asList(c1, c2, c3));

        BigDecimal expected = InvoiceCalculator.grossAmount(original).amount().add(new BigDecimal("60.50"));
        assertEquals(0, expected.compareTo(result.amount()));
    }
    @Test
    public void effectiveGross_multipleCorrectionsSummed_equalsMinusAll(){
        ZonedDateTime issued = ZonedDateTime.of(2026, 1, 15, 10, 0, 0, 0, ZoneId.of("UTC"));
        Invoice original = invoice("INV-204", "Base", issued, "1000.00", "EUR", false);
        CorrectionInvoice c1 = correction("25.00", "EUR",CorrectionInvoiceType.REFUND);
        CorrectionInvoice c2 = correction("75.00", "EUR",CorrectionInvoiceType.REFUND);

        Money result = InvoiceCalculator.effectiveGross(original,Arrays.asList(c1,c2));
        BigDecimal expected = InvoiceCalculator.grossAmount(original).amount().subtract(new BigDecimal("100"));
        assertEquals(expected,result.amount());
    }
    @Test(expected = ProcessRefundException.class)
    public void effectiveGross_multipleCorrectionsSummed_throwPre(){
        ZonedDateTime issued = ZonedDateTime.of(2026, 1, 15, 10, 0, 0, 0, ZoneId.of("UTC"));
        Invoice original = invoice("INV-204", "Base", issued, "100.00", "EUR", false);
        CorrectionInvoice c1 = correction("40.00", "EUR",CorrectionInvoiceType.REFUND);
        CorrectionInvoice c2 = correction("90.00", "EUR",CorrectionInvoiceType.REFUND);

        InvoiceCalculator.effectiveGross(original,Arrays.asList(c1,c2));

    }
    @Test
    public void effectiveGross_multipleCorrectionsSummed_equalsOriginalPlusAll() {
        ZonedDateTime issued = ZonedDateTime.of(2026, 1, 15, 10, 0, 0, 0, ZoneId.of("UTC"));
        Invoice original = invoice("INV-204", "Base", issued, "1000.00", "EUR", false);
        CorrectionInvoice c1 = correction("25.00", "EUR",CorrectionInvoiceType.SURCHARGE);
        CorrectionInvoice c2 = correction("75.50", "EUR",CorrectionInvoiceType.SURCHARGE);

        Money result = InvoiceCalculator.effectiveGross(original, Arrays.asList(c1, c2));

        BigDecimal expected = InvoiceCalculator.grossAmount(original).amount().add(new BigDecimal("100.50"));
        assertEquals(0, expected.compareTo(result.amount()));
    }

    @org.junit.jupiter.api.Test
    public void effectiveGross_correctionWithDifferentCurrency_throwsIllegalArgumentWithMessage() {
        ZonedDateTime issued = ZonedDateTime.of(2026, 1, 15, 10, 0, 0, 0, ZoneId.of("UTC"));
        Invoice original = invoice("INV-205", "Base", issued, "1000.00", "EUR", false);
        CorrectionInvoice badCurrency = correction("50.00", "USD",CorrectionInvoiceType.SURCHARGE);

        try {
            InvoiceCalculator.effectiveGross(original, Arrays.asList(badCurrency));
            fail("expected IllegalArgumentException for currency mismatch");
        } catch (IllegalArgumentException e) {
            assertEquals("Not all invoice have the same currency.", e.getMessage());
        }
    }

    @Test(expected = NullPointerException.class)
    public void effectiveGross_nullCorrectionsList_throwsNpe() {
        ZonedDateTime issued = ZonedDateTime.of(2026, 1, 15, 10, 0, 0, 0, ZoneId.of("UTC"));
        Invoice original = invoice("INV-206", "Base", issued, "1000.00", "EUR", false);

        InvoiceCalculator.effectiveGross(original, null);
    }

}