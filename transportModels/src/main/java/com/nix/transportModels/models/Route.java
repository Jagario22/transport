package com.nix.transportModels.models;

public final class Route {
    private final long id;
    private final long from_id;
    private final long to_id;
    private final int cost;

    public Route(long id, long from_id, long to_id, int cost) {
        this.id = id;
        this.from_id = from_id;
        this.to_id = to_id;
        this.cost = cost;
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

    public int getCost() {
        return cost;
    }
}
