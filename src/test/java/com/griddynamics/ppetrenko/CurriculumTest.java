package com.griddynamics.ppetrenko;

import org.junit.Test;

import static com.griddynamics.ppetrenko.Course.*;
import static org.junit.Assert.assertEquals;

public class CurriculumTest {

    @Test
    public void checkDurationCalculationWhenCurriculumContainsMoreThanOneCourse() {
        Curriculum curriculum = new Curriculum("AQE", SELENIUM, PAGE_OBJECT, TEST_DESIGN);

        int actual = curriculum.getDuration();

        assertEquals(42, actual);
    }

    @Test
    public void checkDurationCalculationWhenCurriculumContainsOneCourse() {
        Curriculum curriculum = new Curriculum("AQE", SELENIUM);

        int actual = curriculum.getDuration();

        assertEquals(16, actual);
    }

    @Test (expected = IllegalStateException.class)
    public void checkThatExceptionIsThrownWhenCreatingCurriculumWithoutCourses() {
        Curriculum curriculum = new Curriculum("AQE");
    }
}
