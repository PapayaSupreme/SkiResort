package passes;


import enums.PassCategory;
import people.Person;



public class SeasonPass extends Pass {

    public SeasonPass(){ /* JPA */ }

    public SeasonPass(Person owner){
        super(owner);
        setPassCategory(PassCategory.SEASON);
    }

    @Override public PassCategory getPassCategory() { return PassCategory.SEASON; }

}
