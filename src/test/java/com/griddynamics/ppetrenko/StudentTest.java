package com.griddynamics.ppetrenko;

import org.junit.Before;
import org.junit.Test;


import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import static com.griddynamics.ppetrenko.MyClock.setClock;
import static java.time.Clock.*;
import static java.time.LocalDateTime.parse;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class StudentTest {
    private Student student;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    Curriculum mockCurriculum;

    @Before
    public void before() {
        mockCurriculum = mock(Curriculum.class);
        student = new Student("Sidorov", "Ivan", mockCurriculum);
        when(mockCurriculum.getDuration()).thenReturn(20);
    }

    // Tests for End Date calculation
    @Test
    public void checkEndDateCalculationWhenEndDatShouldBeNextMonth() {
        Student.setCurriculumStartDate("2021-11-30");

        LocalDateTime actual = student.getCurriculumEndDate();

        assertEquals(parse("2021-12-02 14:00", FORMATTER), actual);
    }

    @Test
    public void checkEndDateCalculationWhenEndDatShouldBeNextYear() {
        Student.setCurriculumStartDate("2021-12-31");

        LocalDateTime actual = student.getCurriculumEndDate();

        assertEquals(parse("2022-01-04 14:00", FORMATTER), actual);
    }

    @Test
    public void checkEndDateWhenItsTimeShouldBeAtLastHourOfSchoolDayOnFriday() {
        Student.setCurriculumStartDate("2021-11-17");
        when(mockCurriculum.getDuration()).thenReturn(24);

        LocalDateTime actual = student.getCurriculumEndDate();

        assertEquals(parse("2021-11-19 18:00", FORMATTER), actual);
    }

    @Test
    public void checkEndDateWhenItsTimeShouldBeAtLastHourOfSchoolDayNotOnFriday() {
        Student.setCurriculumStartDate("2021-11-16");
        when(mockCurriculum.getDuration()).thenReturn(24);

        LocalDateTime actual = student.getCurriculumEndDate();

        assertEquals(parse("2021-11-18 18:00", FORMATTER), actual);
    }

    @Test
    public void checkEndDateWhenItsTimeShouldBeOneHourAfterSchoolDayStartOnMonday() {
        Student.setCurriculumStartDate("2021-11-17");
        when(mockCurriculum.getDuration()).thenReturn(25);

        LocalDateTime actual = student.getCurriculumEndDate();

        assertEquals(parse("2021-11-22 11:00", FORMATTER), actual);
    }

    @Test
    public void checkEndDateWhenItsTimeShouldBeOneHourAfterSchoolDayStartNotOnMonday() {
        Student.setCurriculumStartDate("2021-11-16");
        when(mockCurriculum.getDuration()).thenReturn(25);

        LocalDateTime actual = student.getCurriculumEndDate();

        assertEquals(parse("2021-11-19 11:00", FORMATTER), actual);
    }

    @Test
    public void dayLightSavingEventShouldNotAffectEndDateCalculation() {
        Student.setCurriculumStartDate("2021-10-29");

        LocalDateTime actual = student.getCurriculumEndDate();

        assertEquals(parse("2021-11-02 14:00", FORMATTER), actual);
    }

    @Test
    public void checkEndDateCalculationWhenItShouldBe29OfFebruaryInLeapYear() {
        Student.setCurriculumStartDate("2024-02-27");

        LocalDateTime actual = student.getCurriculumEndDate();

        assertEquals(parse("2024-02-29 14:00", FORMATTER), actual);
    }

    @Test
    public void checkEndDateCalculationWhenItShouldBeFirstOfMarchInNotLeapYear() {
        Student.setCurriculumStartDate("2022-02-25");

        LocalDateTime actual = student.getCurriculumEndDate();

        assertEquals(parse("2022-03-01 14:00", FORMATTER), actual);
    }

    @Test
    public void verifyEndDateCalculationWhenStartDateIsSaturday() {
        Student.setCurriculumStartDate("2021-11-20");

        LocalDateTime actual = student.getCurriculumEndDate();

        assertEquals(parse("2021-11-24 14:00", FORMATTER), actual);
    }

    @Test
    public void verifyEndDateCalculationWhenStartDateIsSunday() {
        Student.setCurriculumStartDate("2021-11-21");

        LocalDateTime actual = student.getCurriculumEndDate();

        assertEquals(parse("2021-11-24 14:00", FORMATTER), actual);
    }

    @Test
    public void endDateShouldBeMovedFromSaturdayToMonday() {
        Student.setCurriculumStartDate("2021-11-18");

        LocalDateTime actual = student.getCurriculumEndDate();

        assertEquals(parse("2021-11-22 14:00", FORMATTER), actual);
    }

    @Test
    public void verifyEndDateCalculationWhenCurriculumDurationIsLessThanOneDay() {
        Student.setCurriculumStartDate("2021-11-18");
        when(mockCurriculum.getDuration()).thenReturn(4);

        LocalDateTime actual = student.getCurriculumEndDate();

        assertEquals(parse("2021-11-18 14:00", FORMATTER), actual);
    }

    // Test for isCurriculumFinishedBoolean
    @Test
    public void isCurriculumFinishedIsFalseWhenOneSecondLeftToEndDate() {
        Student.setCurriculumStartDate("2021-11-15");
        setClock(fixed(Instant.parse("2021-11-17T13:59:59Z"), ZoneId.of("UTC")));

        boolean actual = student.isCurriculumFinished();

        assertFalse("isCurriculumFinished is true when one second is left to end date", actual);
    }

    @Test
    public void isCurriculumFinishedIsTrueWhenEndDateIsNow() {
        Student.setCurriculumStartDate("2021-11-15");
        setClock(fixed(Instant.parse("2021-11-17T14:00:00Z"), ZoneId.of("UTC")));

        boolean actual = student.isCurriculumFinished();

        assertTrue("isCurriculumFinished is false when end date is now", actual);
    }

    @Test
    public void isCurriculumFinishedIsTrueWhenOneSecondPassedSinceEndDate() {
        Student.setCurriculumStartDate("2021-11-15");
        setClock(fixed(Instant.parse("2021-11-17T14:00:01Z"), ZoneId.of("UTC")));

        boolean actual = student.isCurriculumFinished();

        assertTrue("isCurriculumFinished is false when 1 second has passed since end date", actual);
    }

    // Tests for calculation of days and hours gap between end date and now
    @Test
    public void checkGapCalculationWhenExactlyOneHourPassedSinceEndDate() {
        Student.setCurriculumStartDate("2021-11-15");
        setClock(fixed(Instant.parse("2021-11-17T15:00:00Z"), ZoneId.of("UTC")));

        int actualDays = student.getDaysGapBetweenNowAndCurriculumEndDate();
        int actualHours = student.getRemainderHoursGapBetweenNowAndCurriculumEndDate();

        assertEquals("Wrong numbers of days", 0, actualDays);
        assertEquals("Wrong numbers of hours", 1, actualHours);
    }

    @Test
    public void checkGapCalculationWhen23Hours59MinutesAndSecondsLeftToEndDate() {
        Student.setCurriculumStartDate("2021-11-15");
        setClock(fixed(Instant.parse("2021-11-16T14:00:01Z"), ZoneId.of("UTC")));

        int actualDays = student.getDaysGapBetweenNowAndCurriculumEndDate();
        int actualHours = student.getRemainderHoursGapBetweenNowAndCurriculumEndDate();

        assertEquals("Wrong numbers of days", 0, actualDays);
        assertEquals("Wrong numbers of hours", 23, actualHours);
    }

    @Test
    public void checkGapCalculationWhen59MinutesAndSecondsPassedSinceEndDate() {
        Student.setCurriculumStartDate("2021-11-15");
        setClock(fixed(Instant.parse("2021-11-17T14:59:59Z"), ZoneId.of("UTC")));

        int actualDays = student.getDaysGapBetweenNowAndCurriculumEndDate();
        int actualHours = student.getRemainderHoursGapBetweenNowAndCurriculumEndDate();

        assertEquals("Wrong numbers of days", 0, actualDays);
        assertEquals("Wrong numbers of hours", 0, actualHours);
    }

    @Test
    public void checkGapCalculationWhenExactlyOneDayPassedSinceEndDate() {
        Student.setCurriculumStartDate("2021-11-15");
        setClock(fixed(Instant.parse("2021-11-18T14:00:00Z"), ZoneId.of("UTC")));

        int actualDays = student.getDaysGapBetweenNowAndCurriculumEndDate();
        int actualHours = student.getRemainderHoursGapBetweenNowAndCurriculumEndDate();

        assertEquals("Wrong numbers of days", 1, actualDays);
        assertEquals("Wrong numbers of hours", 0, actualHours);
    }

    @Test
    public void checkGapCalculationWhenOneDayOneHourAndOneSecondPassedSinceEndDate() {
        Student.setCurriculumStartDate("2021-11-15");
        setClock(fixed(Instant.parse("2021-11-18T15:00:01Z"), ZoneId.of("UTC")));

        int actualDays = student.getDaysGapBetweenNowAndCurriculumEndDate();
        int actualHours = student.getRemainderHoursGapBetweenNowAndCurriculumEndDate();

        assertEquals("Wrong numbers of days", 1, actualDays);
        assertEquals("Wrong numbers of hours", 1, actualHours);
    }

    @Test
    public void checkGapCalculationWhenTwoDaysAndOneSecondLeftToEndDate() {
        Student.setCurriculumStartDate("2021-11-15");
        setClock(fixed(Instant.parse("2021-11-15T13:59:59Z"), ZoneId.of("UTC")));

        int actualDays = student.getDaysGapBetweenNowAndCurriculumEndDate();
        int actualHours = student.getRemainderHoursGapBetweenNowAndCurriculumEndDate();

        assertEquals("Wrong numbers of days", 2, actualDays);
        assertEquals("Wrong numbers of hours", 0, actualHours);
    }

    @Test
    public void checkGapCalculationWhenOneDay23Hours59MinutesAndSecondsLeftToEndDate() {
        Student.setCurriculumStartDate("2021-11-15");
        setClock(fixed(Instant.parse("2021-11-15T14:00:01Z"), ZoneId.of("UTC")));

        int actualDays = student.getDaysGapBetweenNowAndCurriculumEndDate();
        int actualHours = student.getRemainderHoursGapBetweenNowAndCurriculumEndDate();

        assertEquals("Wrong numbers of days", 1, actualDays);
        assertEquals("Wrong numbers of hours", 23, actualHours);
    }

    @Test
    public void checkGapCalculationWhenMoreThanOneDayAndMoreThanOneHourPassedSinceEndDate() {
        Student.setCurriculumStartDate("2021-11-15");
        setClock(fixed(Instant.parse("2021-11-21T17:29:21Z"), ZoneId.of("UTC")));

        int actualDays = student.getDaysGapBetweenNowAndCurriculumEndDate();
        int actualHours = student.getRemainderHoursGapBetweenNowAndCurriculumEndDate();

        assertEquals("Wrong numbers of days", 4, actualDays);
        assertEquals("Wrong numbers of hours", 3, actualHours);
    }

    @Test
    public void checkGapCalculationWhenGetRemainderHoursGapIsCalledFirst() { // data is the same as in test above
        Student.setCurriculumStartDate("2021-11-15");
        setClock(fixed(Instant.parse("2021-11-21T17:29:21Z"), ZoneId.of("UTC")));

        int actualHours = student.getRemainderHoursGapBetweenNowAndCurriculumEndDate();
        int actualDays = student.getDaysGapBetweenNowAndCurriculumEndDate();

        assertEquals("Wrong numbers of days", 4, actualDays);
        assertEquals("Wrong numbers of hours", 3, actualHours);
    }

    @Test
    public void checkGapCalculationWhenEndDateIsNow() {
        Student.setCurriculumStartDate("2021-11-15");
        setClock(fixed(Instant.parse("2021-11-17T14:00:00Z"), ZoneId.of("UTC")));

        int actualDays = student.getDaysGapBetweenNowAndCurriculumEndDate();
        int actualHours = student.getRemainderHoursGapBetweenNowAndCurriculumEndDate();

        assertEquals("Wrong numbers of days", 0, actualDays);
        assertEquals("Wrong numbers of hours", 0, actualHours);
    }

    @Test
    public void checkGapCalculationWhenMoreThanYearPassedSinceEndDate() {
        Student.setCurriculumStartDate("2021-11-15");
        setClock(fixed(Instant.parse("2022-11-18T16:23:11Z"), ZoneId.of("UTC")));

        int actualDays = student.getDaysGapBetweenNowAndCurriculumEndDate();
        int actualHours = student.getRemainderHoursGapBetweenNowAndCurriculumEndDate();

        assertEquals("Wrong numbers of days", 366, actualDays);
        assertEquals("Wrong numbers of hours", 2, actualHours);
    }
}
