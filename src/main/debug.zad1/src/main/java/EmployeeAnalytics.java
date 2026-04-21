import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EmployeeAnalytics {

    public static void main(String[] args) {
        List<Employee> employees = List.of(
                new Employee("Anna", "IT", 8500, 5),
                new Employee("Bartek", "IT", 5000, 1),
                new Employee("Celina", "HR", 7200, 4),
                new Employee("Dawid", "HR", 4800, 2),
                new Employee("Ewa", "IT", 9200, 7),
                new Employee("Filip", "Marketing", 6500, 3),
                new Employee("Grażyna", "Marketing", 4200, 1),
                new Employee("Henryk", "HR", 8000, 6),
                new Employee("Iga", "IT", 4500, 0),
                new Employee("Jan", "Marketing", 7800, 5)
        );

        // Cel: średnia pensja DOŚWIADCZONYCH pracowników (>= 3 lata) w każdym departamencie
        // BUG: filtr < 3 zamiast >= 3 (bierze juniorów zamiast seniorów!)
        Map<String, Double> avgSalaryByDept = employees.stream()
                .filter(e -> e.getYearsOfExperience() >= 3)
                .collect(Collectors.groupingBy(
                        Employee::getDepartment,
                        Collectors.averagingDouble(Employee::getSalary)
                ));

        System.out.println("=== Średnia pensja doświadczonych pracowników (>= 3 lata) ===");
        avgSalaryByDept.forEach((dept, avg) ->
                System.out.printf("  %s: %.2f zł%n", dept, avg));

        // Dodatkowa analiza: najlepiej zarabiający w każdym departamencie
        Map<String, Employee> topEarners = employees.stream()
                .collect(Collectors.groupingBy(
                        Employee::getDepartment,
                        Collectors.collectingAndThen(
                                Collectors.maxBy(Comparator.comparingDouble(Employee::getSalary)),
                                opt -> opt.orElse(null)
                        )
                ));

        System.out.println("\n=== Najlepiej zarabiający w departamencie ===");
        topEarners.forEach((dept, emp) ->
                System.out.printf("  %s: %s (%.2f zł)%n", dept, emp.getName(), emp.getSalary()));
    }

    static class Employee {
        private String name;
        private String department;
        private double salary;
        private int yearsOfExperience;

        Employee(String name, String department, double salary, int yearsOfExperience) {
            this.name = name;
            this.department = department;
            this.salary = salary;
            this.yearsOfExperience = yearsOfExperience;
        }

        String getName() { return name; }
        String getDepartment() { return department; }
        double getSalary() { return salary; }
        int getYearsOfExperience() { return yearsOfExperience; }
    }
    /*
1. Którzy pracownicy przeszli przez filtr `< 3`? Wymień ich imiona.
Bartek,Dawid,Grażyna,Iga
2. Czy to są doświadczeni pracownicy (≥ 3 lata)? Co jest nie tak z filtrem?
nie, ponizej 3
3. Jak naprawić filtr? Napisz poprawny warunek.
                .filter(e -> e.getYearsOfExperience() >= 3)
4. Co dokładnie pokazuje Stream Debugger (Trace Current Stream Chain)? Dlaczego jest przydatny?
pokazuje wynik z calej lamby, pierwsze kto zostal przefiltrowany i wynik z groupowania
     */
}
