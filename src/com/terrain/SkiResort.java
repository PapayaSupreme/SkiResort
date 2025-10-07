package com.terrain;

import com.people.Employee;
import com.people.Guest;
import com.people.Instructor;
import com.utils.IDGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SkiResort {
    private final String name;
    private final long id;
    private UUID publicId;
    private final List<SkiArea> skiAreas = new ArrayList<>();
    private final List<Guest> guests = new ArrayList<>();
    private final List<Instructor> instructors = new ArrayList<>();
    private final List<Employee> employees = new ArrayList<>();

    public SkiResort(String name, long id) {
        this.name = name;
        this.id = IDGenerator.generateID();
        this.publicId = UUID.randomUUID();
    }

    public String getName() { return this.name; }
    public long getId() { return this.id; }
    public UUID getPublicId() { return publicId; }

    public List<SkiArea> getSkiAreas() { return List.copyOf(this.skiAreas); }
    public List<Employee> getEmployees() { return List.copyOf(this.employees); }
    public List<Guest> getGuests() { return List.copyOf(this.guests); }
    public List<Instructor> getInstructors() { return List.copyOf(this.instructors); }

    public void addSkiArea(SkiArea skiArea) { this.skiAreas.add(skiArea); }
    public void addEmployee(Employee employee) { this.employees.add(employee); }
    public void addGuest(Guest guest) { this.guests.add(guest); }
    public void addInstructor(Instructor instructor) { this.instructors.add(instructor); }

    public boolean removeSkiArea(SkiArea skiArea) { return this.skiAreas.remove(skiArea); }
    public void removeEmployee(Employee employee) { this.employees.remove(employee); }
    public void removeGuest(Guest guest) { this.guests.remove(guest); }
    public void removeInstructor(Instructor instructor) { this.instructors.remove(instructor); }

    @Override
    public String toString() {
        StringBuilder a = new StringBuilder("Ski resort: name=" +  this.name + ", id=" + this.id + "ski areas=");
        for (SkiArea skiArea: this.skiAreas) {
            a.append(skiArea.toString()).append("\n");
        }
        a.append("\n==== LIFTS ====\n\n");
        for (SkiArea skiArea: this.skiAreas) {
            a.append(skiArea.getName()).append(":\n");
            for (Lift lift: skiArea.getLifts()) {
                a.append(lift.toString()).append("\n");
            }
            a.append("\n");
        }
        a.append("\n==== SLOPES ====\n\n");
        for (SkiArea skiArea: this.skiAreas) {
            a.append(skiArea.getName()).append(":\n");
            for (Slope slope: skiArea.getSlopes()) {
                a.append(slope.toString()).append("\n");
            }
            a.append("\n");
        }
        a.append("\n==== RESCUE POINTS ====\n\n");
        for (SkiArea skiArea: this.skiAreas) {
            a.append(skiArea.getName()).append(":\n");
            for (RescuePoint rescuePoint: skiArea.getRescuePoints()) {
                a.append(rescuePoint.toString()).append("\n");
            }
            a.append("\n");
        }
        a.append("\n==== RESTAURANTS ====\n\n");
        for (SkiArea skiArea: this.skiAreas) {
            a.append(skiArea.getName()).append(":\n");
            for (Restaurant restaurant: skiArea.getRestaurants()) {
                a.append(restaurant.toString()).append("\n");
            }
            a.append("\n");
        }
        a.append("\n==== SUMMITS ====\n\n");
        for (SkiArea skiArea: this.skiAreas) {
            a.append(skiArea.getName()).append(":\n");
            for (Summit summit: skiArea.getSummits()) {
                a.append(summit.toString()).append("\n");
            }
            a.append("\n");
        }
        return a.toString();
    }
}
