package com.nix.transport.database.models;

public final class Location {
    private final long id;
    private final String name;

    public Location (long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
