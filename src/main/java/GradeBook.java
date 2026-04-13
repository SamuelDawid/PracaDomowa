import java.util.Arrays;

public class GradeBook {

    static final String[] STUDENTSS = {
  "Ala Kowalska", "Bartek Nowak", "Celina Zielińska", "Damian Wójcik"};

    static final String[] SUBJECTS = {
            "Matematyka", "Polski", "Angielski", "Historia", "Fizyka"};

// wiersz = uczeń, kolumna = przedmiot; 0 = brak oceny, 1-6 skala

    static final int[][] GRADES = {
            { 5, 4, 6, 3, 5 },
            { 3, 3, 4, 2, 0 },
            { 6, 5, 5, 6, 6 },
            { 2, 1, 3, 2, 3 }
    };
    static double average(int[] grades){
        //— średnia z tablicy, **pomija `0`** (brak oceny). Dla pustej / samych zer — zwraca `-1.0
        int numberOfGrades = 0;
        int sum = 0;

        for (int grade : grades) {
            sum += grade;
            if (grade != 0)
                numberOfGrades++;
        }

        if(sum == 0 )  return -1.0;

        return (double) sum / numberOfGrades;
    }
    static double averageForStudent(int[][] grades, int studentIndex){
        //przeciążenie, używa `average(int[])
        return average( grades[studentIndex]);

    }
    static double averageForSubject(int[][] grades, int subjectIndex){
        //średnia z kolumny
        int[] avrageForSub = new int[grades.length];
        for (int i = 0; i < grades.length; i++) {
            avrageForSub[i] = grades[i][subjectIndex];
        }
        return average(avrageForSub);
    }
    static int maxRecursive(int[][] grades){
        //znalezienie maksymalnej oceny w całej macierzy **rekurencyjnie**
        // (zakaz pętli w tej metodzie; pomocnicza metoda prywatna dozwolona)

        return max(grades,grades.length -1,grades[grades.length -1].length -1);
    }

    private static int max(int[][] array,int row,int col){

        if(row == 0 && col == 0){
            return array[0][0];
        }
        else if (col == 0){

            return Math.max(array[row][col],max(array,row -1,array[row -1].length -1));
        }else{
            return Math.max(array[row][col],max(array,row,col -1));
        }
    }
    static String classify(Double average){

        switch (average){
            case Double avg when avg <= 0 ->{ return "brak ocen";}
            case Double avg when avg < 2.0 ->{ return "zagrożony";}
            case Double avg when avg < 3.5 ->{ return "przeciętny";}
            case Double avg when avg < 4.5 ->{ return "dobry";}
            case Double avg when avg < 5.5 ->{ return "bardzo dobry";}
            default ->{ return "celujący";}
        }

    }
    static void printBasicReport(){
        for (int i = 0; i < STUDENTSS.length ; i++) {
            System.out.println(
                    STUDENTSS[i].split(" ")[0] +" "+ averageForStudent(GRADES,i)+" " + classify(average(GRADES[i]))
            );
        }
        System.out.println("Globalna max ocena: " + maxRecursive(GRADES));
    }

    }
    /*

6. `` — drukuje dla każdego ucznia linię: `imię | średnia | klasyfikacja`
oraz na końcu globalną maksymalną ocenę znalezioną przez `maxRecursive`.
     */

