package com.passes;

import com.enums.PassCategory;
import com.enums.PassStatus;

import java.time.Instant;

public abstract class Pass {
    private final int id;
    private final int ownerId;
    private final String lastName;
    private final String firstName;
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

    public int getId() { return this.id; }
    public int getOwnerId() { return this.ownerId; }
    public String getLastName() { return this.lastName; }
    public String getFirstName() { return this.firstName; }
    public PassStatus getPassStatus() { return this.passStatus; }
    public PassCategory getPassCategory() { return this.passCategory; }
    public Instant getCreatedAt() { return this.createdAt; }

    public void setPassStatus(PassStatus passStatus) { this.passStatus = passStatus; }
    public void setPassCategory(PassCategory passCategory) { this.passCategory = passCategory; }

    public void activate() { this.passStatus = PassStatus.ACTIVE; }
    public void deactivate() { this.passStatus = PassStatus.SUSPENDED; }

    public abstract boolean isValidAt(Instant at);
    public abstract double getPrice();
}
