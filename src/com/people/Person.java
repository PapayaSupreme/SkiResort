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
    private final List<Pass> passes = new ArrayList<>();
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
    public UUID getPublicId() { return publicId; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public LocalDate getDob() { return dob; }
    public Instant getCreatedAt() { return createdAt; }

    public List<Pass> getPasses() { return List.copyOf(this.passes); }

    public void addPass(Pass pass) { this.passes.add(pass); }

    public boolean removePass(Pass pass) { return this.passes.remove(pass); }

    @Override
    public String toString() {
        return "Person: id=" + id + ", publicId=" + publicId +
                ", name='" + firstName + " " + lastName + '\'' +
                ", dob=" + dob;
    }
}
