package passes;

import enums.PassCategory;
import people.Person;

public class ALaCartePass extends Pass {

    public ALaCartePass(){ /* JPA*/ }

    public ALaCartePass(Person owner) {
        super(owner);
        setPassCategory(PassCategory.ALACARTE);
    }

    @Override public PassCategory getPassCategory() { return PassCategory.ALACARTE; }


}
