import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class GradeBookTest {

    @org.junit.jupiter.api.Test
    void movingAverage() {
        int[] grades = {1, 2, 3, 4, 5};
        int window = 3;

        assertEquals(new double[] {2.0, 3.0, 4.0},GradeBook.movingAverage(grades,window));
    }

    @org.junit.jupiter.api.Test
    void canBePromoted() {

    }

    @org.junit.jupiter.api.Test
    void extractGrades() {
        String[][] parsed = GradeBook.parseCsv(GradeBook.CSV_DATA);

        int[][] grades = {
                {5, 4, 6, 3, 5}
                , {3, 3, 4, 2, 0}
                , {6, 5, 5, 6, 6}
                , {2, 1, 3, 2, 3}
                , {4, 4, 5, 4, 5}};
        assertArrayEquals(grades, GradeBook.extractGrades(parsed));
    }

    @org.junit.jupiter.api.Test
    void extractNames() {
        String[][] parsed = GradeBook.parseCsv(GradeBook.CSV_DATA);

        String[] result = {"Ala Kowalska", "Bartek Nowak", "Celina Zielińska", "Damian Wójcik", "Ewelina Lis"};

        assertArrayEquals(result, GradeBook.extractNames(parsed));
    }

    @org.junit.jupiter.api.Test
    void rankStudentsByAverage() {
       String[] names = {"Alice", "Bob", "Charlie"};
        int[][] grades = {
        {100, 90},   // avg = 95
        {80, 70},    // avg = 75
        {90, 90}     // avg = 90
        };
        int[] result = {0, 2, 1};
        assertArrayEquals(result,GradeBook.rankStudentsByAverage(names,grades));
    }
    @org.junit.jupiter.api.Test
    void rankStudentsByAverageTie(){
        String[] names = {"A", "B", "C"};
        int[][] grades = {
                {90, 90},
                {80, 70},
                {70, 70}
        };
        int[] result = GradeBook.rankStudentsByAverage(names,grades);
        // last must be index 2
        assertEquals(2,result[2]);

        assertTrue(
                (result[0] == 0 && result[1] ==1) || (result[0] == 1 && result[1] == 0)
        );
    }
    @org.junit.jupiter.api.Test
    void quicksortIndices() {
        int[] idx = {0, 1, 2};
        double[] keys = {95.0, 75.0, 90.0};

        GradeBook.quicksortIndices(idx, keys, 0, idx.length - 1);

        assertArrayEquals(new int[]{0, 2, 1}, idx);
    }

    @org.junit.jupiter.api.Test
    void median() {
        int[] grades = {1,2,3}; // sorted

        assertEquals(2.0,GradeBook.median(grades));
    }
    @org.junit.jupiter.api.Test
    void medianEvenNumbers() {
        int[] grades = {4,1,2,3}; // notsorted

        assertEquals(2.5,GradeBook.median(grades));
    }
    @org.junit.jupiter.api.Test
    void medianSingleElement() {
        int[] grades = {4}; // notsorted

        assertEquals(4,GradeBook.median(grades));
    }

    @org.junit.jupiter.api.Test
    void stddev() {
        int[] grades = {2, 4, 4, 4, 5, 5, 7, 9}; // mean = 5

        assertEquals(2.0,GradeBook.stddev(grades));
    }

    @org.junit.jupiter.api.Test
    void takeOutZeros() {
        int[] array = {1, 2, 0, 3, 2, 0};
        int[] result = {1, 2, 3, 2};

        assertArrayEquals(result, GradeBook.takeOutZeros(array));
    }

    @org.junit.jupiter.api.Test
    void average() {
        int[] grades = {5, 4, 6, 3, 5};
        double result = 4.6;

        assertEquals(result, GradeBook.average(grades));
    }

    @org.junit.jupiter.api.Test
    void averageForStudent() {

        double result = 4.6;
        assertEquals(result, GradeBook.averageForStudent(GradeBook.GRADES, 0));
    }

    @org.junit.jupiter.api.Test
    void averageForSubject() {
        double result = 4.0;

        assertEquals(result, GradeBook.averageForSubject(GradeBook.GRADES, 0));
    }

    @org.junit.jupiter.api.Test
    void maxRecursive() {
        int result = 6;
        assertEquals(result, GradeBook.maxRecursive(GradeBook.GRADES));

    }

    @org.junit.jupiter.api.Test
    void classify() {
        String result = "bardzo dobry";
        assertEquals(result, GradeBook.classify(5.4));
    }
}