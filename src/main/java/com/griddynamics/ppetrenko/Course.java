package com.griddynamics.ppetrenko;

public enum Course {
    JAVA(16), JDBC(24), SPRING (16),
    TEST_DESIGN(10), PAGE_OBJECT(16), SELENIUM(16);

    private int duration;

    Course (int duration) {
        if (duration < 1)
            throw new IllegalStateException("Duration of the some course is less than 1 hour");
        this.duration = duration;
    }

    public int getDuration() {
        return duration;
    }
}
