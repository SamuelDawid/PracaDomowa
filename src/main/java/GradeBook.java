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
    static final String CSV_DATA = """
        name,Matematyka,Polski,Angielski,Historia,Fizyka
        Ala Kowalska,5,4,6,3,5
        Bartek Nowak,3,3,4,2,0
        Celina Zielińska,6,5,5,6,6
        Damian Wójcik,2,1,3,2,3
        Ewelina Lis,4,4,5,4,5
        """;

    static String[][] parseCsv(String csv){
    //parsuje text block do **jagged array** stringów (pierwszy wiersz = nagłówek, reszta = wiersze danych).
        int startIndex = 0,row =0,col =0;
        boolean firstRow = true;
        for (int i = 0; i < csv.length(); i++) {

            if(csv.charAt(i) == ',' && firstRow)
                col++;
            else if (csv.charAt(i) == '\n') {
                firstRow = false;
                row++;
            }
        }
        String[][] jaggedArray = new String[row ][col +1];
        int rowCount = 0,columnCount = 0;
        for (int i = 0; i < csv.length(); i++) {
            if(csv.charAt(i) == ','){
                jaggedArray[rowCount][columnCount] = csv.substring(startIndex,i);
                startIndex = i +1;
                columnCount++;
            } else if (csv.charAt(i) == '\n') {
                jaggedArray[rowCount][columnCount] = csv.substring(startIndex,i);
                startIndex = i +1;
                rowCount++;
                columnCount =0;
            }
        }
        return jaggedArray;
    }
    static int[][] extractGrades(String[][] parsed){
    // z surowej macierzy stringów wyciąga tylko kolumny ocen (pomija nagłówek i kolumnę z nazwiskiem), konwertuje na `int`.

        int[][] grades = new int[parsed.length -1][parsed[0].length-1];

        for (int i = 0; i < parsed.length -1; i++) {
            for (int j = 0; j < parsed[parsed.length -1].length -1; j++) {
                grades[i][j] = Integer.parseInt(parsed[i + 1][j +1]);
            }
        }
        return grades;

    }
    static String[] extractNames(String[][] parsed){

        String[] names = new String[parsed.length -1];
        for (int i = 1; i < parsed.length ; i++) {
            names[i -1] = parsed[i][0];
        }
        return names;
    }
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

