package com.rgabay.embedded_sdn_sproc.procedure;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.procedure.Context;
import org.neo4j.procedure.Mode;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.Procedure;

import java.util.stream.Stream;

/**
 * Created by rossgabay on 5/25/17.
 */
public class PersonProc {
    private static final int DEFAULT_RECO_LIMIT = 10;

    @Context
    public GraphDatabaseService database;

    @Procedure(name = "com.rgabay.sprocnode", mode = Mode.READ)
    public Stream<NodeResult> getRecommendations(@Name("label") String label) {

        ResourceIterator<Node> input = database.findNodes(Label.label(label));
        if (null == input) {
            return Stream.empty();
        }

        return input.stream().map(NodeResult::new);
    }

    public static class NodeResult {
        public Node node;

        NodeResult(Node node) {
            this.node = node;
        }
    }

}
