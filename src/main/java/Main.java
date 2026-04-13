public class Main {


    public static void main(String[] args) {
        int[] grades = new int[]{ 5, 4, 6, 3, 5 };
        System.out.println(GradeBook.average(grades));
        System.out.println(GradeBook.averageForStudent(GradeBook.GRADES,1));
        System.out.println(GradeBook.averageForSubject(GradeBook.GRADES,1));
        int[] grade = new int[]{1 , 4 , 2 , 7 , 3};
        System.out.println(GradeBook.classify(5.6d));
        GradeBook.printBasicReport();
    }
}
