package com.nix.transport.transportView;

import com.nix.transport.database.models.Problem;
import com.nix.transport.database.models.Solution;
import com.nix.weightedGraph.GraphWeighted;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class Solver {
    public List<Solution> getSolutions(List<Problem> problems, GraphWeighted graph) {
        if (problems.size() == 0) {
            log.info("there are no problems to solve");
            return null;
        }

        final List<Solution> solutions = new ArrayList<>();
        int cost;
        int id = 1;
        for (Problem problem : problems) {
            cost = graph.findCostOfShortestPath(String.valueOf(problem.getFrom_id()), String.valueOf(problem.getTo_id()));
            solutions.add(new Solution(id++, cost));
        }

        return solutions;
    }
}
