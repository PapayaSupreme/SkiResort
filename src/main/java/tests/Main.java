package tests;

import enums.EmployeeType;
import people.Employee;
import people.Guest;
import people.PersonRepo;
import utils.JPA;

import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        var repo = new PersonRepo();

        // INSERT
        var g = new Guest("Pablo", "ferreira", LocalDate.of(2004,11,11));
        repo.save(g);

        Employee e = new Employee("Pablo", "ferreira", LocalDate.of(2004,11,11), EmployeeType.PISTER, 1200000L);
        repo.save(e);

        // SELECT
        repo.findAll().forEach(p ->
                System.out.println(p.getId() + " " + p.getFirstName() + " " + p.getLastName())
        );

        JPA.close();
    }
}
