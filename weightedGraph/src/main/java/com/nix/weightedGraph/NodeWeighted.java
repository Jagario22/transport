package com.nix.weightedGraph;

import java.util.LinkedList;

public class NodeWeighted {
    private int n;
    private String name;
    private boolean visited;
    private LinkedList<EdgeWeighted> edges;

    public NodeWeighted(int n, String name) {
        this.n = n;
        this.name = name;
        visited = false;
        edges = new LinkedList<>();
    }

    boolean isVisited() {
        return visited;
    }

    void visit() {
        visited = true;
    }

    void unvisit() {
        visited = false;
    }

    public int getN() {
        return n;
    }

    public String getName() {
        return name;
    }

    public LinkedList<EdgeWeighted> getEdges() {
        return edges;
    }


}
