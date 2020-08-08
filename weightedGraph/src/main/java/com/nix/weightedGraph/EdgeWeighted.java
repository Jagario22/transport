package com.nix.weightedGraph;

public class EdgeWeighted {
    private NodeWeighted source;
    private NodeWeighted destination;
    private Integer weight;

    EdgeWeighted(NodeWeighted s, NodeWeighted d, int w) {

        source = s;
        destination = d;
        weight = w;
    }

    public int compareTo(EdgeWeighted otherEdge) {
        if (this.weight > otherEdge.weight) {
            return 1;
        }
        else return -1;
    }

    public NodeWeighted getSource() {
        return source;
    }

    public NodeWeighted getDestination() {
        return destination;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}
