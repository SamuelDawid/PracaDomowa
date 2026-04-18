package BillingServices;

import records.Invoice;
import records.Money;
import records.SplitPaymentBreakdown;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static StaticFinal.StaticFinalValues.*;

public class BillingService {
    public static Map<YearMonth, Money> monthlyGrossInPln(List<Invoice> invoices, ExchangeRateTable rates){

        Map<YearMonth,Money> result = new TreeMap<>();
        for (Invoice inv : invoices){
            result.merge(YearMonth.from(inv.issuedAt()),
                    rates.convertTo(InvoiceCalculator.grossAmount(inv),BStargetCurrency,inv.issuedAt().toLocalDate()),
                    (valOne,valTwo) -> new Money(valOne.amount().add(valTwo.amount()),valOne.currency())
                    );
        }

        return result;
    }
    static Money lateInterest(Invoice invoice, LocalDate paidOn, BigDecimal annualRate){
        if(ChronoUnit.DAYS.between(invoice.dueDate(),paidOn) <= 0) return new Money(BigDecimal.ZERO,invoice.netAmount().currency());
        long daysOverdue = ChronoUnit.DAYS.between(invoice.dueDate(),paidOn);
        BigDecimal interestAmount = (InvoiceCalculator.grossAmount(invoice).amount().multiply(annualRate)
                .multiply(new BigDecimal(daysOverdue)))
                .divide(days,2,RoundingMode.HALF_EVEN);

        return new Money(interestAmount,invoice.netAmount().currency());
    }

    public static String renderMonthlyReport(YearMonth month, List<Invoice> invoices, ExchangeRateTable rates, ZoneId displayZone) {
    /*
        // TO JEST MOJ KOD NA DOLE JEST POPRAWIONE PRZEZ LLM ZEBY FORMATOWANIE BYLO TAKIE SAME !!
        Map<YearMonth, Money> monthlyGross = monthlyGrossInPln(invoices, rates);
        BigDecimal monthlyGrossAmount = monthlyGross.get(month).amount();
        Map<String, Money> originalCurrencies = new HashMap<>();
        Map<String, Integer> totalInvPerCurrency = new HashMap<>();
        Map<String, Money> groupedClients = new HashMap<>();

        for (Invoice inv : invoices) {
            originalCurrencies.merge(inv.netAmount().currency(), inv.netAmount(),
                    (valOne, valTwo) -> new Money(valOne.amount().add(valTwo.amount()), valOne.currency()));
            totalInvPerCurrency.merge(inv.netAmount().currency(), 1, Integer::sum);
            groupedClients.merge(inv.clientName(), inv.netAmount(),
                    (valOne, valTwo) -> new Money(valOne.amount().add(valTwo.amount()), valOne.currency())
            );
        }
        StringBuilder header = new StringBuilder("""
                ╔══════════════════════════════════════════════════╗
                ║ Monthly Report: %s (zone %s)     ║
                ╠══════════════════════════════════════════════════╣
                """.formatted(month, displayZone));
        StringBuilder totalInvoices = new StringBuilder("""
                ║ Total invoices: %d                              ║
                ║ Total gross in PLN: %,.2f                    ║
                ╠══════════════════════════════════════════════════╣
                """.formatted(invoices.size(),
                (monthlyGrossAmount == null) ? new BigDecimal("brak Invoicow") : monthlyGrossAmount));

        StringBuilder originalCurrenciesString = new StringBuilder("""
                ║ Original currencies:                             ║
                """);
        for (Map.Entry<String, Money> entry : originalCurrencies.entrySet()) {
            originalCurrenciesString.append("║ ").append(entry.getKey()).append(": ").append(totalInvPerCurrency.get(entry.getKey()))
                    .append(totalInvPerCurrency.get(entry.getKey()) == 1 ? " invoice, " : " invoices, ")
                    .append(entry.getValue().amount()).append(" ").append(entry.getValue().currency()).append("║").append("\n");
        }
        originalCurrenciesString.append("╠══════════════════════════════════════════════════╣").append("\n");
        StringBuilder topThreeString = new StringBuilder("""
                ║ Top 3 clients (by gross in PLN):                 ║
                """);
        List<Map.Entry<String, Money>> topThree =
                groupedClients.entrySet().stream()
                        .sorted((v1, v2) -> v2.getValue().amount().compareTo(v1.getValue().amount())).limit(3).toList();
        for (int i = 0; i < topThree.size(); i++) {
            topThreeString.append("║ ").append(i + 1).append(". ").append(topThree.get(i).getKey()).append(".....")
                    .append(topThree.get(i).getValue().amount()).append(" ").append(topThree.get(i).getValue().currency()).append(" ║").append("\n");
        }
        topThreeString.append("╚══════════════════════════════════════════════════╝");
        return header.append(totalInvoices).append(originalCurrenciesString).append(topThreeString).toString();
        */

            final int W = 50; // szerokość wnętrza między ║ i ║
            Map<YearMonth, Money> monthlyGross = monthlyGrossInPln(invoices, rates);
            BigDecimal monthlyGrossAmount = monthlyGross.get(month).amount();
            Map<String, Money> originalCurrencies = new LinkedHashMap<>();
            Map<String, Integer> totalInvPerCurrency = new HashMap<>();
            Map<String, Money> groupedClients = new HashMap<>();

            for (Invoice inv : invoices) {
                originalCurrencies.merge(inv.netAmount().currency(), inv.netAmount(),
                        (a, b) -> new Money(a.amount().add(b.amount()), a.currency()));
                totalInvPerCurrency.merge(inv.netAmount().currency(), 1, Integer::sum);
                groupedClients.merge(inv.clientName(), inv.netAmount(),
                        (a, b) -> new Money(a.amount().add(b.amount()), a.currency()));
            }

            // pomocnicze linie ramki
            String top       = "╔" + "═".repeat(W) + "╗\n";
            String separator = "╠" + "═".repeat(W) + "╣\n";
            String bottom    = "╚" + "═".repeat(W) + "╝\n";

            // każda linia treści: ║ + content wyrównany do W znaków + ║
            // używamy %-Ws żeby leftpad spacjami do stałej szerokości
            StringBuilder sb = new StringBuilder();
            sb.append(top);

            sb.append(String.format("║%-" + W + "s║%n",
                    String.format(" Monthly Report: %s (zone %s)", month, displayZone)));

            sb.append(separator);

            sb.append(String.format("║%-" + W + "s║%n",
                    String.format(" Total invoices: %d", invoices.size())));

            sb.append(String.format("║%-" + W + "s║%n",
                    String.format(" Total gross in PLN: %,.2f", monthlyGrossAmount)));

            sb.append(separator);

            sb.append(String.format("║%-" + W + "s║%n", " Original currencies:"));
            for (Map.Entry<String, Money> entry : originalCurrencies.entrySet()) {
                int count = totalInvPerCurrency.get(entry.getKey());
                String invoiceWord = count == 1 ? "invoice, " : "invoices,";
                // kwota wyrównana do prawej w polu 10 znaków, waluta 3
                String line = String.format("   %s: %d %s %10.2f %s",
                        entry.getKey(), count, invoiceWord,
                        entry.getValue().amount(), entry.getValue().currency());
                sb.append(String.format("║%-" + W + "s║%n", line));
            }
            sb.append(separator);
            sb.append(String.format("║%-" + W + "s║%n", " Top 3 clients (by gross in PLN):"));
            List<Map.Entry<String, Money>> topThree =
                    groupedClients.entrySet().stream()
                            .sorted((v1, v2) -> v2.getValue().amount().compareTo(v1.getValue().amount()))
                            .limit(3)
                            .toList();

            final int AMOUNT_WIDTH = 16; // np. "15,320.00 PLN" + padding

            for (int i = 0; i < topThree.size(); i++) {
                String name   = topThree.get(i).getKey();
                String amount = String.format("%,.2f %s",
                        topThree.get(i).getValue().amount(),
                        topThree.get(i).getValue().currency());

                String prefix        = String.format("  %d. ", i + 1);          // "  1. "
                String amountPadded  = String.format("%" + AMOUNT_WIDTH + "s", amount); // prawostronny padding kwoty

                // wypełniamy kropkami wolne miejsce między nazwą a kwotą
                int dotsCount = W - prefix.length() - name.length() - amountPadded.length() - 2; // -2 za spacje wokół kropek
                String dots = " " + ".".repeat(Math.max(dotsCount, 1)) + " ";

                sb.append(String.format("║%-" + W + "s║%n",
                        prefix + name + dots + amountPadded));
            }
            sb.append(bottom);

            return sb.toString();
        }


    }



