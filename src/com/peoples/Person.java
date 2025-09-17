package com.peoples;

import java.util.ArrayList;

public abstract class Person {
    private final int id;
    private final List<Pass> passes = new ArrayList<>();

    protected Person(int id) {
        this.id = id;
    }

    public int getId() { return this.id; }
    public List<Pass> getPasses() { return this.passes; }

    public void addPass(Pass pass) { this.passes.add(pass); }

    public boolean removePass(Pass pass) { return this.passes.remove(pass); }
}
