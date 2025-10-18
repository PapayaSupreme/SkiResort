package passes;

import enums.PassKind;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import people.Person;

import java.time.LocalDate;

@Entity
@DiscriminatorValue("ALACARTE")
public class ALaCartePass extends Pass {

    public ALaCartePass(){ /* JPA*/ }

    public ALaCartePass(Person owner) {
        super(owner);
        setPassKind(PassKind.ALACARTE);
    }

    @Override public PassKind getPassKind() { return PassKind.ALACARTE; }

    @Override
    public String toString(){
        return "ALaCartePass: "+ super.toString();
    }
}
