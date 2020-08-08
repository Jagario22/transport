package com.nix.weightedGraph;

import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class GraphWeighted {
    private Set<NodeWeighted> nodes;
    private boolean directed;

    public GraphWeighted(boolean directed) {
        this.directed = directed;
        nodes = new HashSet<>();
    } 

    public void addEdge(NodeWeighted source, NodeWeighted destination, int weight) {
        nodes.add(source);
        nodes.add(destination);

        addEdgeHelper(source, destination, weight);

        if (!directed && source != destination) {
            addEdgeHelper(destination, source, weight);
        }
    }

    private void addEdgeHelper(NodeWeighted a, NodeWeighted b, int weight) {
        for (EdgeWeighted edge : a.getEdges()) {
            if (edge.getSource() == a && edge.getDestination() == b) {
                edge.setWeight(weight);
                return;
            }
        }
        a.getEdges().add(new EdgeWeighted(a, b, weight));
    }

    private void makeUnVisitedNodes() {
        for (NodeWeighted n : nodes)
            n.unvisit();
    }

    public Integer findCostOfShortestPath(String startIndex, String endIndex) {
        return new DijkstraAlgorithm().findShortestPath(startIndex, endIndex);
    }

    private class DijkstraAlgorithm {
        private HashMap<NodeWeighted, Integer> shortestPathMap;
        NodeWeighted start;
        HashMap<NodeWeighted, NodeWeighted> changedAt;

        public Integer findShortestPath(String startIndex, String endIndex) {
            start = findNodeByName(startIndex);
            NodeWeighted end = findNodeByName(endIndex);

            if (start == null || end == null)
                return null;

            this.changedAt = new HashMap<>();
            changedAt.put(start, null);

            this.shortestPathMap = new HashMap<>();

            fillMap();
            findAllEdges();
            start.visit();

            while (true) {
                NodeWeighted currentNode = closestReachableUnvisited(shortestPathMap);

                if (currentNode == null) {
                    log.debug("There isn't a path between " + start.getName() + " and " + end.getName());
                    return null;
                }

                if (currentNode == end) {
                    log.debug("The path with the smallest weight between "
                            + start.getName() + " and " + end.getName() + " is:");

                    NodeWeighted child = end;
                    String path = end.getName();

                    while (true) {
                        NodeWeighted parent = changedAt.get(child);
                        if (parent == null) {
                            break;
                        }

                        path = parent.getName() + " " + path;
                        child = parent;
                    }
                    log.debug(path);
                    log.debug("The path costs: " + shortestPathMap.get(end));
                    makeUnVisitedNodes();
                    return shortestPathMap.get(end);
                }
                currentNode.visit();

                for (EdgeWeighted edge : currentNode.getEdges()) {
                    if (edge.getDestination().isVisited())
                        continue;

                    if (shortestPathMap.get(currentNode)
                            + edge.getWeight()
                            < shortestPathMap.get(edge.getDestination())) {
                        shortestPathMap.put(edge.getDestination(),
                                shortestPathMap.get(currentNode) + edge.getWeight());
                        changedAt.put(edge.getDestination(), currentNode);
                    }
                }
            }
        }

        private NodeWeighted findNodeByName(String name) {
            for (NodeWeighted n : nodes) {
                if (n.getName().equals(name))
                    return n;
            }

            return null;
        }

        private void findAllEdges() {
            for (EdgeWeighted edge : start.getEdges()) {
                shortestPathMap.put(edge.getDestination(), edge.getWeight());
                changedAt.put(edge.getDestination(), start);
            }
        }
        private void fillMap() {
            for (NodeWeighted node : nodes) {
                if (node == start)
                    shortestPathMap.put(start, 0);
                else shortestPathMap.put(node, Integer.MAX_VALUE);
            }
        }

        private NodeWeighted closestReachableUnvisited(HashMap<NodeWeighted, Integer> shortestPathMap) {
            double shortestDistance = Double.POSITIVE_INFINITY;
            NodeWeighted closestReachableNode = null;
            for (NodeWeighted node : nodes) {
                if (node.isVisited())
                    continue;

                double currentDistance = shortestPathMap.get(node);
                if (currentDistance == Double.POSITIVE_INFINITY)
                    continue;

                if (currentDistance < shortestDistance) {
                    shortestDistance = currentDistance;
                    closestReachableNode = node;
                }
            }
            return closestReachableNode;
        }
    }
}
