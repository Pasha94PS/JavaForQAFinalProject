package com.griddynamics.ppetrenko;

import com.github.stefanbirkner.systemlambda.SystemLambda;
import org.junit.*;


import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.ZoneId;

import static com.griddynamics.ppetrenko.MyClock.setClock;
import static java.time.Clock.fixed;
import static java.time.Instant.parse;
import static org.junit.Assert.assertEquals;


public class StudentReportRunnerSystemTests {
    private static final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private static final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private Student[] students;

    @BeforeClass
    public static void setUpStreams() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @Before
    public void before() {
        Curriculum AQE = new Curriculum("AQE", Course.TEST_DESIGN, Course.PAGE_OBJECT, Course.SELENIUM);
        Student student1 = new Student("Sidorov", "Ivan", AQE);
        Curriculum JavaDeveloper = new Curriculum("Java Developer", Course.JAVA, Course.JDBC, Course.SPRING);
        Student student2 = new Student("Ivan", "Ivanov", JavaDeveloper);
        students = new Student[] {student1, student2};
    }

    @Test
    public void shortReportIsShownWhenThereIsNoReportArgument() {
        String[] args = {"2021-11-11"};
        setClock(fixed(parse("2021-11-19T12:00:00.00Z"), ZoneId.of("UTC")));

        StudentReportRunner.run(args, students);

        assertEquals("Ivan Sidorov (AQE) - Training completed. 1 day has passed since the end.\n" +
                        "Ivanov Ivan (Java Developer) - Training is not finished. 6 hours are left until the end.\n",
                outContent.toString());
        assertEquals("", errContent.toString());
    }

    @Test
    public void shortReportIsShownWhen0IsSpecifiedAsReportArgument() {
        String[] args = {"2021-11-11", "0"};
        setClock(fixed(parse("2021-11-19T18:00:00.00Z"), ZoneId.of("UTC")));

        StudentReportRunner.run(args, students);

        assertEquals("Ivan Sidorov (AQE) - Training completed. 1 day 6 hours have passed since the end.\n" +
                        "Ivanov Ivan (Java Developer) - Training completed. Less than hour has passed since the end.\n",
                outContent.toString());
        assertEquals("", errContent.toString());
    }

    @Test
    public void longReportIsShownWhenNot0IsSpecifiedAsReportArgument() {
        String[] args = {"2021-11-11", "1"};
        setClock(fixed(parse("2021-11-17T10:30:00.00Z"), ZoneId.of("UTC")));

        StudentReportRunner.run(args, students);

        assertEquals("-----------------------------------------------------------------------------\n" +
                "Name: Ivan Sidorov\n" +
                "Working time: from 10 to 18\n" +
                "Program name: AQE\n" +
                "Program duration: 42 hours\n" +
                "Start date: 11 November 2021, Thursday, 10:00\n" +
                "End date: 18 November 2021, Thursday, 12:00\n" +
                "Status: Training is not finished. 1 day 1 hour is left until the end.\n" +
                "-----------------------------------------------------------------------------\n" +
                "Name: Ivanov Ivan\n" +
                "Working time: from 10 to 18\n" +
                "Program name: Java Developer\n" +
                "Program duration: 56 hours\n" +
                "Start date: 11 November 2021, Thursday, 10:00\n" +
                "End date: 19 November 2021, Friday, 18:00\n" +
                "Status: Training is not finished. 2 days 7 hours are left until the end.\n" +
                "-----------------------------------------------------------------------------\n",
                outContent.toString());
        assertEquals("", errContent.toString());
    }

    @Test
    public void errorMessageIsShownAndAppIsStoppedWhenNoArgumentsWereSpecified() throws Exception {
        String[] args = new String[0];

        int status = SystemLambda.catchSystemExit(() -> StudentReportRunner.run(args, students));

        assertEquals(1, status);
        assertEquals("Error: You have missed to specify the start date of the course\n" +
                "Please set the start date as a first argument when launching\n", errContent.toString());
        assertEquals("", outContent.toString());
    }

    @Test
    public void errorMessageIsShownAndAppIsStoppedWhenDateArgumentHasWrongFormat() throws Exception {
        String[] args = {"11:11:2021"};

        int status = SystemLambda.catchSystemExit(() -> StudentReportRunner.run(args, students));

        assertEquals(1, status);
        assertEquals("Error: Format of the specified start date is incorrect\n" +
                "Please specify the date following the format yyyy-mm-dd\n", errContent.toString());
        assertEquals("", outContent.toString());
    }

    @After
    public void resetStreams() {
        outContent.reset();
        errContent.reset();
    }

    @AfterClass
    public static void restoreStreams() {
        System.setOut(System.out);
        System.setErr(System.err);
    }
}
