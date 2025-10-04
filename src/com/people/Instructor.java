package com.people;

import com.enums.SkiSchool;

public class Instructor extends Guest {
    private SkiSchool school;
    private Worksite worksite;

    public Instructor(int id, SkiSchool school, Worksite worksite) {
        super(id);
        this.school = school;
        this.worksite = worksite;
    }

    public SkiSchool getSchool() { return this.school; }
    public Worksite getWorksite() { return this.worksite; }

    public void setSchool(SkiSchool school) { this.school = school; }
    public void setWorksite(Worksite worksite) { this.worksite = worksite; }
}
