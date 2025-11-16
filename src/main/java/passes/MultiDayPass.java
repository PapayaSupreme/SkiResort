package passes;

import enums.PassKind;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import people.Person;

import java.time.LocalDate;

@Entity
@DiscriminatorValue("MULTIDAY")
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
        setPassKind(PassKind.MULTIDAY);
    }

    public LocalDate getValidFrom() { return this.validFrom; }
    public LocalDate getValidTo() { return this.validTo; }

    @Override public PassKind getPassKind(){ return PassKind.MULTIDAY; }

    @Override
    public String toString(){
        return "MultiDayPass - validity period: [" + this.validFrom + " - "  + this.validTo + "], " + super.toString();
    }
}
