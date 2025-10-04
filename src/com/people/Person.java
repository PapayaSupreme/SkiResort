package com.people;

import com.passes.Pass;

import java.util.List;
import java.util.ArrayList;

public abstract class Person {
    private final int id;
    private final List<Pass> passes = new ArrayList<>();

    protected Person(int id) {
        this.id = id;
    }

    public int getId() { return this.id; }
    public List<Pass> getPasses() { return List.copyOf(this.passes); }

    public void addPass(Pass pass) { this.passes.add(pass); }

    public boolean removePass(Pass pass) { return this.passes.remove(pass); }
}
