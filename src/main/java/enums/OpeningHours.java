package enums;

import java.time.LocalTime;

public record OpeningHours(LocalTime opening, LocalTime closing) {

    @Override
    public String toString(){
        return this.opening + " - " + this.closing;
    }
}
