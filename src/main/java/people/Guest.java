package people;

import enums.PersonKind;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.time.LocalDate;

@Entity
@DiscriminatorValue("GUEST")
public class Guest extends Person {

    protected Guest() { /* JPA */ }

    public Guest(String email, String firstName, String lastName, LocalDate dob) {
        super(email, firstName, lastName, dob);
        setPersonKind(PersonKind.GUEST);
    }

    public static Guest of(String email, String firstName, String lastName, LocalDate dob) {
        return new Guest(email, firstName, lastName, dob);
    }

    @Override public PersonKind getPersonKind(){ return PersonKind.GUEST; }

    @Override
    public void setWorksiteId(Long worksiteId) {
        super.setWorksiteId(null);
    }

    @Override
    public String toString(){
        return "Guest - " + super.toString();
    }
}
