package com.griddynamics.ppetrenko;

public class Curriculum {

    private String name;
    private Course[] courses;

    Curriculum(String name, Course... courses) {
        if (courses.length == 0)
            throw new IllegalStateException("Curriculum should contain at least one course");
        this.name = name;
        this.courses = courses;
    }

    public String getName() {
        return this.name;
    }

    public int getDuration() {
        int duration = 0;
        for (Course course : courses) {
            duration += course.getDuration();
        }
        return duration;
    }
}
