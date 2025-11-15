package passes;

import enums.PassKind;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import people.Person;


@Entity
@DiscriminatorValue("SEASON")
public class SeasonPass extends Pass {

    public SeasonPass(){ /* JPA */ }

    public SeasonPass(Person owner){
        super(owner);
        setPassKind(PassKind.SEASON);
    }

    @Override public PassKind getPassKind() { return PassKind.SEASON; }

    @Override
    public String toString(){
        return "SeasonPass - "+ super.toString();
    }
}
