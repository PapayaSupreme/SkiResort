package com.passes;

import com.enums.PassCategory;
import com.enums.PassStatus;

import java.time.Instant;
import java.util.Date;

public abstract class Pass {
    private final int id;
    private final int ownerId;
    private String lastName = "";
    private String firstName = "";
    private PassStatus passStatus = PassStatus.ACTIVE;
    private PassCategory passCategory;
    private final Instant createdAt;

    public Pass(int id, int ownerId, String lastName, String firstName, PassCategory passCategory) {
        this.id = id;
        this.ownerId = ownerId;
        this.lastName = lastName;
        this.firstName = firstName;
        this.passCategory = passCategory;
        this.createdAt = Instant.now();
    }

    public int getId() { return id; }
    public int getOwnerId() { return ownerId; }
    public String getLastName() { return lastName; }
    public String getFirstName() { return firstName; }
    public PassStatus getPassStatus() { return passStatus; }
    public PassCategory getPassCategory() { return passCategory; }
    public Instant getCreatedAt() { return createdAt; }

    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setPassStatus(PassStatus passStatus) { this.passStatus = passStatus; }
    public void setPassCategory(PassCategory passCategory) { this.passCategory = passCategory; }

    public void activate() { this.passStatus = PassStatus.ACTIVE; }
    public void deactivate() { this.passStatus = PassStatus.SUSPENDED; }

    public abstract boolean isValid(Date date);
    public abstract double getPrice();
}
