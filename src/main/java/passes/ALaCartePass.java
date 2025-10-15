package passes;

import enums.PassKind;
import people.Person;

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
