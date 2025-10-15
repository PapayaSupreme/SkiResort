package passes;


import enums.PassKind;
import people.Person;



public class SeasonPass extends Pass {

    public SeasonPass(){ /* JPA */ }

    public SeasonPass(Person owner){
        super(owner);
        setPassKind(PassKind.SEASON);
    }

    @Override public PassKind getPassKind() { return PassKind.SEASON; }

    @Override
    public String toString(){
        return "SeasonPass: "+ super.toString();
    }
}
