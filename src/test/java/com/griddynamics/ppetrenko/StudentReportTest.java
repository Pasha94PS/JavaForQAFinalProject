package com.griddynamics.ppetrenko;

import org.junit.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


// This test suit is focused on testing only the student status string building. Report type definition is not tested here
// because it is covered by StudentReportRunnerSystemTests suit. And the student status string is the same in both
// report types. So for better readability only short type of the report is used here.
public class StudentReportTest {
    private static final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private Student student;
    private StudentReport studentReport;
    private static final String NON_STATUS_PART_OF_REPORT = "Ivanov Ivan (AQE) - ";

    @BeforeClass
    public static void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @Before
    public void before() {
        student = mock(Student.class);
        studentReport = new StudentReport(student);
        when(student.getName()).thenReturn("Ivan");
        when(student.getSurname()).thenReturn("Ivanov");
        Curriculum curriculum = mock(Curriculum.class);
        when(student.getCurriculum()).thenReturn(curriculum);
        when(curriculum.getName()).thenReturn("AQE");
    }

    @Test
    public void verifyReportWhenMoreThanOneDayAndExactlyOneHourPassedSinceCurriculumEnd() {
        when(student.getDaysGapBetweenNowAndCurriculumEndDate()).thenReturn(5);
        when(student.getRemainderHoursGapBetweenNowAndCurriculumEndDate()).thenReturn(1);
        when(student.isCurriculumFinished()).thenReturn(true);

        studentReport.printReport();

        assertEquals(NON_STATUS_PART_OF_REPORT + "Training completed. 5 days 1 hour has passed since the end.\n",
                outContent.toString());

    }

    @Test
    public void verifyReportWhenOneDayAndTwoHoursLeftToCurriculumEnd() {
        when(student.getDaysGapBetweenNowAndCurriculumEndDate()).thenReturn(1);
        when(student.getRemainderHoursGapBetweenNowAndCurriculumEndDate()).thenReturn(2);
        when(student.isCurriculumFinished()).thenReturn(false);

        studentReport.printReport();

        assertEquals(NON_STATUS_PART_OF_REPORT + "Training is not finished. 1 day 2 hours are left until the end.\n",
                outContent.toString());
    }

    @Test
    public void verifyReportWhenOneHourPassedSinceCurriculumEnd() {
        when(student.getDaysGapBetweenNowAndCurriculumEndDate()).thenReturn(0);
        when(student.getRemainderHoursGapBetweenNowAndCurriculumEndDate()).thenReturn(1);
        when(student.isCurriculumFinished()).thenReturn(true);

        studentReport.printReport();

        assertEquals(NON_STATUS_PART_OF_REPORT + "Training completed. 1 hour has passed since the end.\n",
                outContent.toString());
    }

    @Test
    public void verifyReportWhen23HoursLeftToCurriculumEnd() {
        when(student.getDaysGapBetweenNowAndCurriculumEndDate()).thenReturn(0);
        when(student.getRemainderHoursGapBetweenNowAndCurriculumEndDate()).thenReturn(23);
        when(student.isCurriculumFinished()).thenReturn(false);

        studentReport.printReport();

        assertEquals(NON_STATUS_PART_OF_REPORT + "Training is not finished. 23 hours are left until the end.\n",
                outContent.toString());
    }

    @Test
    public void verifyReportWhenOneDayPassedSinceCurriculumEnd() {
        when(student.getDaysGapBetweenNowAndCurriculumEndDate()).thenReturn(1);
        when(student.getRemainderHoursGapBetweenNowAndCurriculumEndDate()).thenReturn(0);
        when(student.isCurriculumFinished()).thenReturn(true);

        studentReport.printReport();

        assertEquals(NON_STATUS_PART_OF_REPORT + "Training completed. 1 day has passed since the end.\n",
                outContent.toString());
    }

    @Test
    public void verifyReportWhenTwoDaysPassedSinceCurriculumEnd() {
        when(student.getDaysGapBetweenNowAndCurriculumEndDate()).thenReturn(2);
        when(student.getRemainderHoursGapBetweenNowAndCurriculumEndDate()).thenReturn(0);
        when(student.isCurriculumFinished()).thenReturn(true);

        studentReport.printReport();

        assertEquals(NON_STATUS_PART_OF_REPORT + "Training completed. 2 days have passed since the end.\n",
                outContent.toString());
    }

    @Test
    public void verifyReportWhenLessThanHourLeftToCurriculumEnd() {
        when(student.getDaysGapBetweenNowAndCurriculumEndDate()).thenReturn(0);
        when(student.getRemainderHoursGapBetweenNowAndCurriculumEndDate()).thenReturn(0);
        when(student.isCurriculumFinished()).thenReturn(false);

        studentReport.printReport();

        assertEquals(NON_STATUS_PART_OF_REPORT + "Training is not finished. Less than hour is left until the end.\n",
                outContent.toString());
    }

    @Test (expected = IllegalArgumentException.class)
    public void exceptionIsThrownWhenNumberOfDaysIsLessThanNull() {
        when(student.getDaysGapBetweenNowAndCurriculumEndDate()).thenReturn(-1);
        when(student.getRemainderHoursGapBetweenNowAndCurriculumEndDate()).thenReturn(0);
        when(student.isCurriculumFinished()).thenReturn(false);

        studentReport.printReport();
    }

    @Test (expected = IllegalArgumentException.class)
    public void exceptionIsThrownWhenNumberOfHoursIsLessThanNull() {
        when(student.getDaysGapBetweenNowAndCurriculumEndDate()).thenReturn(1);
        when(student.getRemainderHoursGapBetweenNowAndCurriculumEndDate()).thenReturn(-1);
        when(student.isCurriculumFinished()).thenReturn(true);

        studentReport.printReport();
    }

    @After
    public void resetStreams() {
        outContent.reset();
    }

    @AfterClass
    public static void restoreStreams() {
        System.setOut(System.out);
        System.setErr(System.err);
    }
}
