package com.nix.transport.database.models;

public final class Problem {
    private final long id;
    private final long from_id;
    private final long to_id;

    public Problem(long id, long from_id, long to_id) {
        this.id = id;
        this.from_id = from_id;
        this.to_id = to_id;
    }

    public long getId() {
        return id;
    }

    public long getFrom_id() {
        return from_id;
    }

    public long getTo_id() {
        return to_id;
    }
}
