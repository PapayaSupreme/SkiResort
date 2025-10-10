package passes;

import enums.PassCategory;
import enums.PassStatus;
import utils.IDGenerator;

import java.time.Instant;
import java.util.UUID;

public abstract class Pass {
    private final long id;
    private final long ownerId;
    private UUID publicId;
    private PassStatus passStatus = PassStatus.ACTIVE;
    private PassCategory passCategory;
    private final Instant createdAt;

    public Pass(long ownerId, PassCategory passCategory) {
        this.id = IDGenerator.generateID();
        this.ownerId = ownerId;
        this.publicId = UUID.randomUUID();
        this.passCategory = passCategory;
        this.createdAt = Instant.now();
    }

    public long getId() { return this.id; }
    public long getOwnerId() { return this.ownerId; }
    public UUID getPublicId() { return publicId; }
    public PassStatus getPassStatus() { return this.passStatus; }
    public PassCategory getPassCategory() { return this.passCategory; }
    public Instant getCreatedAt() { return this.createdAt; }

    public void setPassStatus(PassStatus passStatus) { this.passStatus = passStatus; }
    public void setPassCategory(PassCategory passCategory) { this.passCategory = passCategory; }

    public void activate() { this.passStatus = PassStatus.ACTIVE; }
    public void deactivate() { this.passStatus = PassStatus.SUSPENDED; }

    //TODO: public abstract void createPass(SkiResort skiResort, long ownerId);
    public abstract boolean isValidAt(Instant at);
    public abstract double getPrice();
}
