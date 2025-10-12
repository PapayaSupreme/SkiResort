package people;

import enums.PersonKind;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.time.LocalDate;

@Entity
@DiscriminatorValue("GUEST")
public class Guest extends Person {

    protected Guest() { /* JPA */ }

    public Guest(String firstName, String lastName, LocalDate dob) {
        super(firstName, lastName, dob);
        setWorksiteId(null);
    }

    public static Guest of(String firstName, String lastName, LocalDate dob) {
        return new Guest(firstName, lastName, dob);
    }

    @Override public PersonKind getPersonKind(){ return PersonKind.GUEST; }

    @Override
    public void setWorksiteId(Long worksiteId) {
        super.setWorksiteId(null);
    }
}
