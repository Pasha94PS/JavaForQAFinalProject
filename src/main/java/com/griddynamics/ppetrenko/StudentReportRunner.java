package com.griddynamics.ppetrenko;

import static com.griddynamics.ppetrenko.Course.*;

public class StudentReportRunner {

    public static void main(String[] args) {
        Curriculum AQE = new Curriculum("AQE", TEST_DESIGN, PAGE_OBJECT, SELENIUM);
        Student student1 = new Student("Sidorov", "Ivan", AQE);
        Curriculum JavaDeveloper = new Curriculum("Java Developer", JAVA, JDBC, SPRING);
        Student student2 = new Student("Ivan", "Ivanov", JavaDeveloper);
        run(args, student1, student2);
    }

    public static void run(String[] args, Student... students) {
        try {
            Student.setCurriculumStartDate(args[0]);
        } catch (IndexOutOfBoundsException e) {
            System.err.println("Error: You have missed to specify the start date of the course");
            System.err.println("Please set the start date as a first argument when launching");
            System.exit(1);
        }
        StudentReport studentReport = new StudentReport(students);
        if (args.length > 1)
            studentReport.setReportType(args[1]);
        studentReport.printReport();
    }
}