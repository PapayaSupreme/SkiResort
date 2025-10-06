package com.people;

import com.enums.SkiSchool;

import java.time.LocalDate;

public class Instructor extends Person {
    private SkiSchool school;
    private Worksite worksite;

    public Instructor(String firstName, String lastName, LocalDate dob, SkiSchool school, Worksite worksite) {
        super(firstName, lastName, dob);
        this.school = school;
        this.worksite = worksite;
    }

    public SkiSchool getSchool() { return this.school; }
    public Worksite getWorksite() { return this.worksite; }

    public void setSchool(SkiSchool school) { this.school = school; }
    public void setWorksite(Worksite worksite) { this.worksite = worksite; }

    @Override
    public String toString() {
        return "Instructor: school=" + this.school + ", worksite="
                + this.worksite + " - " + super.toString();
    }
}
