import java.util.Arrays;

public class GradeBook {
    static final String[] STUDENTSS = {
            "Ala Kowalska", "Bartek Nowak", "Celina Zielińska", "Damian Wójcik"};

    static final String[] SUBJECTS = {
            "Matematyka", "Polski", "Angielski", "Historia", "Fizyka"};

// wiersz = uczeń, kolumna = przedmiot; 0 = brak oceny, 1-6 skala

    static final int[][] GRADES = {
            {5, 4, 6, 3, 5},
            {3, 3, 4, 2, 0},
            {6, 5, 5, 6, 6},
            {2, 1, 3, 2, 3}
    };
    static final String CSV_DATA = """
            name,Matematyka,Polski,Angielski,Historia,Fizyka
            Ala Kowalska,5,4,6,3,5
            Bartek Nowak,3,3,4,2,0
            Celina Zielińska,6,5,5,6,6
            Damian Wójcik,2,1,3,2,3
            Ewelina Lis,4,4,5,4,5
            """;

    //region PD01C
    static double[] movingAverage(int[] grades, int window) {
        int[] gradesWithoutZero = takeOutZeros(grades);
        double[] result = new double[gradesWithoutZero.length];

        for (int i = 0; i < result.length; i++) {
            if (i < window - 1) result[i] = -1.0;
            else {
                int sum = 0;
                for (int j = i - (window - 1); j <= i; j++)
                    sum += gradesWithoutZero[j];
                result[i] = (double) sum / window;
            }
        }

        return result;

    }

    static boolean canBePromoted(int[] grades) {
        int[] gradesWithoutZero = takeOutZeros(grades);
        int gradesCountBelowThree = 0;
        double sum = 0;
        boolean isOne = false;
        for (int i = 0; i < gradesWithoutZero.length; i++) {
            if (gradesWithoutZero[i] == 1) isOne = true;
            if (gradesWithoutZero[i] < 3) gradesCountBelowThree++;
            sum += gradesWithoutZero[i];
        }
        if (isOne) return false;
        if ((sum / gradesWithoutZero.length) < 2.5) return false;
        if (gradesCountBelowThree > 2) return false;
        return true;

    }

    static void printRankingTable(String[] names, int[][] grades) {

        int longestName = 0;
        for (String s : names)
            longestName = Math.max(longestName, s.length());

        int[] rankIndex = rankStudentsByAverage(names, grades);
        System.out.println("╔"+ "═".repeat(4)+"╦" + "═".repeat(longestName + 2) + "╦"+"═".repeat(9)+"╦"+"═".repeat(14)+"╗");
        System.out.println("║ Lp ║ " + String.format("%-" + longestName + "s", "Imię") + " ║ Średnia ║ Klasyfikacja ║");
        System.out.println("╠"+ "═".repeat(4)+"╬" + "═".repeat(longestName + 2) + "╬"+"═".repeat(9)+"╬"+"═".repeat(14)+"╣");

        for (int i = 0; i < rankIndex.length; i++) {
            System.out.println("║ "+String.format("%2d",(i +1))+" ║ " +
                    String.format("%-" + longestName + "s", names[rankIndex[i]]) + " ║ "+
                    String.format("%7.2f",averageForStudent(grades, rankIndex[i]))+" ║ "+
                    String.format("%-12s",classify(averageForStudent(grades, rankIndex[i])))+" ║"
            );

        }
        System.out.println("╚"+ "═".repeat(4)+"╩" + "═".repeat(longestName + 2) + "╩"+"═".repeat(9)+"╩"+"═".repeat(14)+"╝");

    }
    //endregion
    //region PD01B

    static String[][] parseCsv(String csv) {
        //parsuje text block do **jagged array** stringów (pierwszy wiersz = nagłówek, reszta = wiersze danych).
        int startIndex = 0, row = 0, col = 0;
        boolean firstRow = true;
        for (int i = 0; i < csv.length(); i++) {

            if (csv.charAt(i) == ',' && firstRow)
                col++;
            else if (csv.charAt(i) == '\n') {
                firstRow = false;
                row++;
            }
        }
        String[][] jaggedArray = new String[row][col + 1];
        int rowCount = 0, columnCount = 0;
        for (int i = 0; i < csv.length(); i++) {
            if (csv.charAt(i) == ',') {
                jaggedArray[rowCount][columnCount] = csv.substring(startIndex, i);
                startIndex = i + 1;
                columnCount++;
            } else if (csv.charAt(i) == '\n') {
                jaggedArray[rowCount][columnCount] = csv.substring(startIndex, i);
                startIndex = i + 1;
                rowCount++;
                columnCount = 0;
            }
        }
        return jaggedArray;
    }

    static int[][] extractGrades(String[][] parsed) {
        // z surowej macierzy stringów wyciąga tylko kolumny ocen (pomija nagłówek i kolumnę z nazwiskiem), konwertuje na `int`.

        int[][] grades = new int[parsed.length - 1][parsed[0].length - 1];

        for (int i = 0; i < parsed.length - 1; i++) {
            for (int j = 0; j < parsed[parsed.length - 1].length - 1; j++) {
                grades[i][j] = Integer.parseInt(parsed[i + 1][j + 1]);
            }
        }
        return grades;

    }

    static String[] extractNames(String[][] parsed) {

        String[] names = new String[parsed.length - 1];
        for (int i = 1; i < parsed.length; i++) {
            names[i - 1] = parsed[i][0];
        }
        return names;
    }

    static int[] rankStudentsByAverage(String[] names, int[][] grades) {
        //zwraca **tablicę indeksów** posortowaną malejąco wg średniej ucznia.
        // Oryginalna macierz i tablica nazwisk **nie są modyfikowane**.
        int[] indexes = new int[names.length];
        double[] avrages = new double[names.length];
        for (int i = 0; i < names.length; i++) {
            avrages[i] = averageForStudent(grades, i);
            indexes[i] = i;
        }
        quicksortIndices(indexes, avrages, 0, indexes.length - 1);
        return indexes;
    }

    static void quicksortIndices(int[] idx, double[] keys, int lo, int hi) {
        if (lo >= hi) return;
        int pivot = partition(idx, keys, lo, hi);
        quicksortIndices(idx, keys, lo, pivot - 1);
        quicksortIndices(idx, keys, pivot + 1, hi);
    }

    static int partition(int[] idx, double[] keys, int lo, int hi) {
        double pivot = keys[idx[hi]];
        int i = lo - 1;
        for (int j = lo; j <= hi - 1; j++) {
            if (keys[idx[j]] > pivot) {
                i++;
                swap(idx, i, j);
            }
        }
        swap(idx, i + 1, hi);
        return i + 1;
    }

    private static void quickSort(int arr[], int begin, int end) {
        if (begin < end) {
            int partitionIndex = partition(arr, begin, end);

            quickSort(arr, begin, partitionIndex - 1);
            quickSort(arr, partitionIndex + 1, end);
        }
    }

    private static int partition(int arr[], int begin, int end) {
        int pivot = arr[end];
        int i = (begin - 1);

        for (int j = begin; j < end; j++) {
            if (arr[j] <= pivot) {
                i++;

                swap(arr, i, j);
            }
        }
        swap(arr, i + 1, end);
        return i + 1;
    }

    static void swap(int[] array, int i, int j) {
        int temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

    static void printGradeHistogram(int[][] grades) {
        int[] gradeCount = new int[7];
        for (int i = 0; i < grades.length; i++) {
            for (int j = 0; j < grades[0].length; j++) {
                if (grades[i][j] != 0) gradeCount[grades[i][j]]++;
            }
        }
        int max = 0, sum = 0;
        for (int i = 1; i < gradeCount.length; i++) {
            max = Math.max(max, gradeCount[i]);
            sum += gradeCount[i];
        }
        System.out.println("Histogram ocen (łącznie " + sum + "):");
        for (int i = 1; i < gradeCount.length; i++) {
            int howMany = max > 40 ? gradeCount[i] * 40 / max : gradeCount[i];
            System.out.print(i + ": ");
            printStar(howMany);
            System.out.print("(" + gradeCount[i] + ")");
            System.out.println();
        }
    }

    private static void printStar(int howMany) {
        for (int i = 0; i < howMany; i++) {
            System.out.print("*");
        }

    }

    static double median(int[] grades) {

        int[] medianGrade = takeOutZeros(grades);
        if (medianGrade.length == 0) return -1;
        quickSort(medianGrade, 0, medianGrade.length - 1);

        if (medianGrade.length % 2 != 0) {
            return medianGrade[medianGrade.length / 2];
        }
        return (medianGrade[medianGrade.length / 2 - 1] + medianGrade[medianGrade.length / 2]) / 2.0;
    }

    static double stddev(int[] grades) {
        int[] avrageGrade = takeOutZeros(grades);
        double avg = average(avrageGrade);
        double sum = 0;
        for (int i = 0; i < avrageGrade.length; i++) {
            sum += (avrageGrade[i] - avg) * (avrageGrade[i] - avg);
        }

        return Math.sqrt(sum / (avrageGrade.length -1));
    }

    static int[] takeOutZeros(int[] grades) {
        int count = 0;
        for (int i = 0; i < grades.length; i++) {
            if (grades[i] != 0) {
                count++;
            }
        }
        int[] arrayToReturn = new int[count];
        int index = 0;
        for (int i = 0; i < grades.length; i++) {
            if (grades[i] != 0) {
                arrayToReturn[index] = grades[i];
                index++;
            }
        }
        return arrayToReturn;

    }

    //endregion
    //region PD01A
    static double average(int[] grades) {
        //— średnia z tablicy, **pomija `0`** (brak oceny). Dla pustej / samych zer — zwraca `-1.0
        int numberOfGrades = 0;
        int sum = 0;

        for (int grade : grades) {
            sum += grade;
            if (grade != 0)
                numberOfGrades++;
        }
        if (sum == 0) return -1.0;
        return (double) sum / numberOfGrades;
    }

    static double averageForStudent(int[][] grades, int studentIndex) {
        //przeciążenie, używa `average(int[])
        return average(grades[studentIndex]);

    }

    static double averageForSubject(int[][] grades, int subjectIndex) {
        //średnia z kolumny
        int[] avrageForSub = new int[grades.length];
        for (int i = 0; i < grades.length; i++) {
            avrageForSub[i] = grades[i][subjectIndex];
        }
        return average(avrageForSub);
    }

    static int maxRecursive(int[][] grades) {
        //znalezienie maksymalnej oceny w całej macierzy **rekurencyjnie**
        // (zakaz pętli w tej metodzie; pomocnicza metoda prywatna dozwolona)

        return max(grades, grades.length - 1, grades[grades.length - 1].length - 1);
    }

    private static int max(int[][] array, int row, int col) {

        if (row == 0 && col == 0) {
            return array[0][0];
        } else if (col == 0) {

            return Math.max(array[row][col], max(array, row - 1, array[row - 1].length - 1));
        } else {
            return Math.max(array[row][col], max(array, row, col - 1));
        }
    }

    static String classify(Double average) {

        switch (average) {
            case Double avg when avg <= 0 -> {
                return "brak ocen";
            }
            case Double avg when avg < 2.0 -> {
                return "zagrożony";
            }
            case Double avg when avg < 3.5 -> {
                return "przeciętny";
            }
            case Double avg when avg < 4.5 -> {
                return "dobry";
            }
            case Double avg when avg < 5.5 -> {
                return "bardzo dobry";
            }
            default -> {
                return "celujący";
            }
        }

    }

    static void printBasicReport() {
        for (int i = 0; i < STUDENTSS.length; i++) {
            System.out.println(
                    STUDENTSS[i].split(" ")[0] + " " + averageForStudent(GRADES, i) + " " + classify(average(GRADES[i]))
            );
        }
        System.out.println("Globalna max ocena: " + maxRecursive(GRADES));
    }
    //endregion
}


