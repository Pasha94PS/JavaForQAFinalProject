package com.griddynamics.ppetrenko;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static java.time.format.DateTimeFormatter.*;

public class StudentReport {
    private final Student[] students;
    private boolean isReportTypeShort = true;
    private static final DateTimeFormatter DATE_FORMAT = ofPattern("d LLLL uuuu, cccc, k:mm", Locale.ENGLISH);

    StudentReport(Student... students) {
        this.students = students;
    }

    public void setReportType(String reportType) {
        this.isReportTypeShort = reportType.equals("0");
    }

    public void printReport() {
        if (isReportTypeShort) {
            for (Student student : students)
                printShortReport(student);
        } else {
            System.out.println("-----------------------------------------------------------------------------");
            for (Student student: students)
                printLongReport(student);
        }
    }

    private void printShortReport(Student student) {
        System.out.printf("%s %s (%s) - %s%n", student.getSurname(), student.getName(), student.getCurriculum().getName(),
                buildStudentCurriculumStatusString(student));
    }

    private void printLongReport(Student student) {
        System.out.printf("Name: %s %s%n", student.getSurname(), student.getName());
        System.out.printf("Working time: from %d to %d%n", Student.getWorkingDayStart(), Student.getWorkingDayEnd());
        System.out.printf("Program name: %s%n", student.getCurriculum().getName());
        System.out.printf("Program duration: %d hours%n", student.getCurriculum().getDuration());
        System.out.printf("Start date: %s%n", Student.getCurriculumStartDate().format(DATE_FORMAT));
        System.out.printf("End date: %s%n", student.getCurriculumEndDate().format(DATE_FORMAT));
        System.out.printf("Status: %s%n", buildStudentCurriculumStatusString(student));
        System.out.println("-----------------------------------------------------------------------------");
    }

    private String buildStudentCurriculumStatusString(Student student) {
        int days = student.getDaysGapBetweenNowAndCurriculumEndDate();
        int hours = student.getRemainderHoursGapBetweenNowAndCurriculumEndDate();
        String daysAndHoursString = buildDaysAndHoursString(days, hours);
        if (student.isCurriculumFinished()) {
            return String.format("Training completed.%s %s passed since the end.",
                    daysAndHoursString,
                    chooseVerbBasedOnNumberOfHoursAndDays(days, hours, "has", "have"));
        }
        else {
            return String.format("Training is not finished.%s %s left until the end.",
                    daysAndHoursString,
                    chooseVerbBasedOnNumberOfHoursAndDays(days, hours, "is", "are"));
        }
    }

    private String buildDaysAndHoursString(int days, int hours) {
        if (days == 0 && hours == 0)
            return " Less than hour";
        return defineNumberOfHoursOrDays(days, "day") + defineNumberOfHoursOrDays(hours, "hour");
    }

    private String defineNumberOfHoursOrDays(int quantity, String singular) {
        if (quantity > 1)
            return String.format(" %d %s", quantity, singular + "s");
        else if (quantity == 1)
            return String.format(" %d %s", quantity, singular);
        else if (quantity == 0)
            return "";
        else
            throw new IllegalArgumentException("Number of " + singular +"s is less than 0");
    }

    private String chooseVerbBasedOnNumberOfHoursAndDays(int days, int hours, String singular, String plural) {
        if (hours > 1) return plural;
        else if (days > 1 && hours == 0) return plural;
        else return singular;
    }
}