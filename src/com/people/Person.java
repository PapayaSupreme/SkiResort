package com.people;

import com.passes.Pass;
import com.utils.IDGenerator;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import java.util.UUID;

public abstract class Person {
    private final long id;
    private UUID publicId;
    private final String firstName;
    private final String lastName;
    private final LocalDate dob;
    private final Instant createdAt;
    protected Person(String firstName, String lastName, LocalDate dob) {
        this.id = IDGenerator.generateID();
        this.publicId = UUID.randomUUID();
        this.firstName = firstName;
        this.lastName = lastName;
        this.dob = dob;
        this.createdAt = Instant.now();
    }

    public long getId() { return this.id; }
    public UUID getPublicId() { return this.publicId; }
    public String getFirstName() { return this.firstName; }
    public String getLastName() { return this.lastName; }
    public LocalDate getDob() { return this.dob; }
    public Instant getCreatedAt() { return this.createdAt; }

    @Override
    public String toString() {
        return "Person: id=" + id + ", name='" + firstName
                + " " + lastName + ", dob=" + dob;
    }
}
