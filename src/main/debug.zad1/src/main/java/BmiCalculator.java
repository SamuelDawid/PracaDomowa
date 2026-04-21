public class BmiCalculator {

    public double calculateBmi(double weight, double height) {
        // BUG: brakuje potęgowania wzrostu!
        double bmi = weight / height;
       // double bmi = weight / Math.pow(height,2);
        return Math.round(bmi * 100.0) / 100.0;
    }

    public String getCategory(double bmi) {
        if (bmi < 18.5) {
            return "Niedowaga";
        } else if (bmi < 25.0) {
            return "Norma";
        } else if (bmi < 30.0) {
            return "Nadwaga";
        } else {
            return "Otyłość";
        }
    }

    public String diagnose(String name, double weight, double height) {
        double bmi = calculateBmi(weight, height);
        String category = getCategory(bmi);
        return name + ": waga=" + weight + " kg, wzrost=" + height
                + " m, BMI=" + bmi + " → " + category;
    }

    public static void main(String[] args) {
        BmiCalculator calc = new BmiCalculator();

        // Osoba 1: oczekiwane BMI = 70 / (1.75 * 1.75) = 22.86 → Norma
        System.out.println(calc.diagnose("Anna", 70.0, 1.75));

        // Osoba 2: oczekiwane BMI = 90 / (1.80 * 1.80) = 27.78 → Nadwaga
        System.out.println(calc.diagnose("Bartek", 90.0, 1.80));

        // Osoba 3: oczekiwane BMI = 55 / (1.70 * 1.70) = 19.03 → Norma
        System.out.println(calc.diagnose("Celina", 55.0, 1.70));
    }
    /*
1. Jaką wartość BMI obliczył program dla Anny? Jaka powinna być prawidłowa wartość?
40.0 → 22.86
2. Dlaczego wzór `weight / height` daje błędny wynik? Jaki powinien być poprawny wzór?
Poniewaz dzielone jest tylko raz przez height a nie sqr hight. bmi = weight / Math.pow(height,2)
3. Dla Bartka -- jaka kategoria została przypisana? Jaka powinna być prawidłowa?
 Otyłość → Nadwaga
4. Wypełnij tabelę:

| Osoba | Otrzymane BMI | Oczekiwane BMI | Otrzymana kategoria | Oczekiwana kategoria |
|-------|--------------|----------------|--------------------|--------------------|
| Anna | 40 | 22.86 | Otyłość | Norma |
| Bartek | 50 | 27.78 | Otyłość | Nadwaga |
| Celina | 32.35 | 19.03 | Otyłość | Norma |

5. double bmi = weight / Math.pow(height,2);
     */
}
