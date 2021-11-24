package com.griddynamics.ppetrenko;

import java.time.*;
import java.time.format.DateTimeParseException;

import static com.griddynamics.ppetrenko.MyClock.*;
import static java.lang.Math.abs;
import static java.time.DayOfWeek.*;
import static java.time.LocalDateTime.*;
import static java.time.LocalDateTime.now;
import static java.time.LocalDateTime.from;
import static java.time.temporal.ChronoUnit.*;
import static java.time.temporal.TemporalAdjusters.next;

public class Student {
    private final String name;
    private final String surname;
    private final Curriculum curriculum;
    private static final int SCHOOL_DAY_START = 10;
    private static final int SCHOOL_DAY_END = 18;
    private static LocalDateTime curriculumStartDate;
    private LocalDateTime curriculumEndDate;
    private Boolean isCurriculumFinished;
    private int absoluteHoursBetweenNowAndEndDate;


    public Student (String name, String surname, Curriculum curriculum) {
        this.name = name;
        this.surname = surname;
        this.curriculum = curriculum;
    }

    public static void setCurriculumStartDate(String startDate) {
        try{
            curriculumStartDate = of(LocalDate.parse(startDate), LocalTime.of(SCHOOL_DAY_START, 0));
        }catch (DateTimeParseException e) {
            System.err.println("Error: Format of the specified start date is incorrect");
            System.err.println("Please specify the date following the format yyyy-mm-dd");
            System.exit(1);
        }
    }

    public String getName() { return name; }

    public String getSurname() { return surname; }

    public Curriculum getCurriculum() { return curriculum; }

    public static int getSchoolDayStart() { return SCHOOL_DAY_START; }

    public static int getSchoolDayEnd() { return SCHOOL_DAY_END; }

    public static LocalDateTime getCurriculumStartDate() { return curriculumStartDate; }

    public LocalDateTime getCurriculumEndDate() {
        if (curriculumEndDate != null)
            return curriculumEndDate;
        return curriculumEndDate = calculateEndDate();
    }

    public boolean isCurriculumFinished() {
        if (isCurriculumFinished != null)
            return isCurriculumFinished;
        return isCurriculumFinished = now(getClock()).isAfter(getCurriculumEndDate()) || now(getClock()).isEqual(curriculumEndDate);
    }

    public int getDaysGapBetweenNowAndCurriculumEndDate() {
        if (absoluteHoursBetweenNowAndEndDate == 0)
            calculateAndSetAbsoluteHoursBetweenNowAndEndDate();
        return absoluteHoursBetweenNowAndEndDate / 24;
    }

    public int getRemainderHoursGapBetweenNowAndCurriculumEndDate() {
        if (absoluteHoursBetweenNowAndEndDate == 0)
            calculateAndSetAbsoluteHoursBetweenNowAndEndDate();
        return absoluteHoursBetweenNowAndEndDate % 24;
    }

    private LocalDateTime calculateEndDate() {
        LocalDateTime timeAccumulator = from(curriculumStartDate);
        int hoursLeft = curriculum.getDuration();
        int step = SCHOOL_DAY_END - SCHOOL_DAY_START;
        while (hoursLeft > 0) {
            if (timeAccumulator.getDayOfWeek().getValue() > 5)
                timeAccumulator = timeAccumulator.with(next(MONDAY));
            if (step < hoursLeft)
                timeAccumulator = timeAccumulator.plusDays(1);
            else
                timeAccumulator = timeAccumulator.plusHours(hoursLeft);
            hoursLeft -= step;
        }
        return timeAccumulator;
    }

    private void calculateAndSetAbsoluteHoursBetweenNowAndEndDate() {
        absoluteHoursBetweenNowAndEndDate = abs((int) HOURS.between(getCurriculumEndDate(), now(getClock())));
    }
}