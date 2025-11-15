package passes;


import enums.PassKind;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import people.Person;

import java.time.LocalDate;


@Entity
@DiscriminatorValue("DAY")
public class DayPass extends Pass{
    @Column(name = "valid_day")
    private LocalDate validDay;

    public DayPass() { /* JPA */ }

    public DayPass(Person owner, LocalDate validDay){
        super(owner);
        setPassKind(PassKind.DAY);
        this.validDay = validDay;
    }

    @Override public PassKind getPassKind(){ return PassKind.DAY; }

    @Override
    public String toString(){
        return "DayPass - validDay=" + this.validDay + ", " + super.toString();
    }
}
