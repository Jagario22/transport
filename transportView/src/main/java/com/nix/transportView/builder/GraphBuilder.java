package com.nix.transportView.builder;

import com.nix.transportModels.models.*;
import com.nix.weightedGraph.GraphWeighted;
import com.nix.weightedGraph.NodeWeighted;

import java.util.ArrayList;
import java.util.List;

public class GraphBuilder {
    private static GraphWeighted graphWeighted = new GraphWeighted(true);
    private static List<NodeWeighted> nodes = new ArrayList<>();

    public static GraphWeighted build(List<Long> locationsId, List<Route> routes) {
        int i = 0;
        for (Long l: locationsId) {
            nodes.add(new NodeWeighted(i++, l.toString()));
        }

        for (Route r: routes) {
            graphWeighted.addEdge(getById(String.valueOf(r.getFrom_id())), getById(String.valueOf(r.getTo_id())), r.getCost());
        }

        return graphWeighted;
    }

    private static NodeWeighted getById(String id) {
        for (NodeWeighted n: nodes) {
            if (n.getName().equals(id))
                return n;
        }
        return null;
    }

}
