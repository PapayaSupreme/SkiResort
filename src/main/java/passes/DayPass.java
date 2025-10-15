package passes;


import enums.PassCategory;
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
        setPassCategory(PassCategory.DAY);
    }

    @Override public PassCategory getPassCategory(){ return PassCategory.DAY; }
}
