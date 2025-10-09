package com.people;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.time.LocalDate;

@Entity
@DiscriminatorValue("GUEST")
public class Guest extends Person {

    protected Guest() { /* JPA */ }

    private Guest(String firstName, String lastName, LocalDate dob) {
        super(firstName, lastName, dob);
    }

    public static Guest of(String firstName, String lastName, LocalDate dob) {
        return new Guest(firstName, lastName, dob);
    }
}
