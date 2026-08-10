import BillingServices.BillingService;
import BillingServices.ExchangeRateTable;
import BillingServices.InvoiceCalculator;
import records.Invoice;
import records.Money;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Main {


    public static void main(String[] args) {
        Invoice inv1 = new Invoice("INV/2024/09/001", "Jan Kowalski",
                new Money(new BigDecimal("150.00"), "PLN"),
                new BigDecimal("0.23"),
                ZonedDateTime.now(), LocalDate.now(),false
        );

        Invoice inv2 = new Invoice("INV/2024/12/002", "Anna Nowak",
                new Money(new BigDecimal("33.33"), "PLN"),
                new BigDecimal("0.23"),
                ZonedDateTime.now(), LocalDate.now(),false);

        Invoice inv3 = new Invoice("INV/2024/11/003", "Firma XYZ",
                new Money(new BigDecimal("0.00"), "PLN"),
                new BigDecimal("0.08"),
                ZonedDateTime.now()
                        , LocalDate.now(),false);



        List<Invoice> tempList = new ArrayList<>(List.of(inv1,inv2,inv3));
        ExchangeRateTable exchangeRateTable = new ExchangeRateTable(ExchangeRateTable.sampleRates().ratesByDate());
        String date = "";
        boolean isClient = false;
        int index= 0;
        for (int i = 0; i < args.length; i++) {
            if(args[i].equals("--month")) date = args[i +1];
            if(args[i].equals("--client")) {
                index = i;
                isClient =true;
            }
        }
        if(!date.isEmpty() ||isClient)
        {
            if(date.isEmpty()) date = YearMonth.now().toString();
            int finalI = index +1;
            List<Invoice> filteredList = tempList.stream().filter(invoice -> invoice.clientName().equals(args[finalI])).toList();
            System.out.println(
                    BillingService.renderMonthlyReport(YearMonth.parse(date),filteredList,exchangeRateTable,ZoneId.of("Europe/Warsaw"))
            );
        }else
            System.out.println(
                    BillingService.renderMonthlyReport(YearMonth.now(),tempList,exchangeRateTable,ZoneId.of("Europe/Warsaw"))
            );
    }
}
