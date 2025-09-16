package com.peoples;

import com.enums.SkiSchool;
import com.terrain.SkiArea;

public class Instructor extends Guest {
    private SkiSchool school;
    private SkiArea worksite;

    public Instructor(int id, SkiSchool school, SkiArea worksite) {
        super(id);
        this.school = school;
        this.worksite = worksite;
    }

    public SkiSchool getSchool() { return this.school; }
    
    public void setSchool(SkiSchool school) { this.school = school; }
}
