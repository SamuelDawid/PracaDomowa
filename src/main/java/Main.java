import java.util.Arrays;

public class Main {


    public static void main(String[] args) {


        String[][] parsCSV = GradeBook.parseCsv(GradeBook.CSV_DATA);
//        System.out.println(Arrays.deepToString(parsCSV));
//        int[][] grades = GradeBook.extractGrades(parsCSV);
//        System.out.println(Arrays.deepToString(grades));
        String[] names = GradeBook.extractNames(parsCSV);
//        System.out.println(Arrays.toString(names));
//        System.out.println(Arrays.toString(GradeBook.rankStudentsByAverage(GradeBook.STUDENTSS, GradeBook.GRADES)));
        GradeBook.printGradeHistogram(GradeBook.extractGrades(parsCSV));
        GradeBook.printRankingTable(GradeBook.extractNames(parsCSV),GradeBook.extractGrades(parsCSV));
        int[] grades = {1, 2, 3, 4, 5};
        System.out.println(Arrays.toString(GradeBook.movingAverage(grades, 3)));
    }
}
