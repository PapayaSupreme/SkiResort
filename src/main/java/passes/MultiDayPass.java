package passes;

import enums.PassCategory;
import jakarta.persistence.Column;
import people.Person;

import java.time.LocalDate;


public class MultiDayPass extends Pass {
    @Column(name = "valid_from")
    private LocalDate validFrom;

    @Column(name = "valid_to")
    private LocalDate validTo;

    public MultiDayPass() { /* JPA */ }

    public MultiDayPass(Person owner, LocalDate validFrom, LocalDate validTo) {
        super(owner);
        this.validFrom = validFrom;
        this.validTo = validTo;
        setPassCategory(PassCategory.MULTIDAY);
    }

    public LocalDate getValidFrom() { return this.validFrom; }
    public LocalDate getValidTo() { return this.validTo; }

    @Override public PassCategory getPassCategory(){ return PassCategory.MULTIDAY; }
}
