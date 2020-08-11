package com.nix.transport.database.models;

public class Solution {
    private final long problem_id;
    private final int cost;

    public Solution(long problem_id, int cost) {
        this.problem_id = problem_id;
        this.cost = cost;
    }

    public long getProblem_id() {
        return problem_id;
    }

    public int getCost() {
        return cost;
    }
}
