/********************************************************************************
 * Copyright (c) 2017 Fraunhofer IEM, Paderborn, Germany
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/
package crypto.visualization;

import crypto.analysis.IAnalysisSeed;
import crypto.analysis.errors.AbstractError;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import org.graphper.api.Cluster;
import org.graphper.api.FileType;
import org.graphper.api.Graphviz;
import org.graphper.api.Line;
import org.graphper.api.Subgraph;
import org.graphper.api.attributes.Rank;
import org.graphper.draw.ExecuteException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Visualizer {

    private static final Logger LOGGER = LoggerFactory.getLogger(Visualizer.class);
    private static final String VISUALIZATION_NAME = "visualization";

    private final File outputFile;

    public Visualizer(String outputDir) throws IOException {
        if (outputDir == null) {
            throw new NullPointerException("OutputDir must not be null");
        }

        this.outputFile = new File(outputDir);

        if (!outputFile.exists()) {
            throw new IOException("Directory " + outputFile.getAbsolutePath() + " does not exist");
        }

        if (!outputFile.isDirectory()) {
            throw new IOException(outputFile.getAbsolutePath() + " is not a directory");
        }
    }

    public void createVisualization(Collection<IAnalysisSeed> seeds) throws IOException {
        Graphviz.GraphvizBuilder builder = Graphviz.digraph();

        Map<AbstractError, WrappedNode> errorToNode = new HashMap<>();
        for (IAnalysisSeed seed : seeds) {
            Map<AbstractError, WrappedNode> errorToNodeForSeed = new HashMap<>();

            for (AbstractError error : seed.getErrors()) {
                WrappedNode node = new WrappedNode(error);

                builder.addNode(node.asGraphicalNode());
                errorToNodeForSeed.put(error, node);
            }

            if (!errorToNodeForSeed.isEmpty()) {
                Cluster cluster = createClusterForSeed(seed, errorToNodeForSeed);
                builder.cluster(cluster);
            }

            errorToNode.putAll(errorToNodeForSeed);
        }

        Collection<Line> lines = createLines(errorToNode);
        for (Line line : lines) {
            builder.addLine(line);
        }

        Graphviz graphviz = builder.build();
        try {
            graphviz.toFile(FileType.PNG).save(outputFile.getAbsolutePath(), VISUALIZATION_NAME);
            LOGGER.info(
                    "Written visualization to {}",
                    outputFile.getAbsolutePath() + File.separator + VISUALIZATION_NAME + ".png");
        } catch (ExecuteException e) {
            LOGGER.error("Could not create visualization: {}", e.getMessage());
        }
    }

    private Cluster createClusterForSeed(
            IAnalysisSeed seed, Map<AbstractError, WrappedNode> errorToNode) {
        Subgraph.SubgraphBuilder subgraphBuilder = Subgraph.builder().rank(Rank.SAME);

        for (AbstractError error : errorToNode.keySet()) {
            subgraphBuilder.addNode(errorToNode.get(error).asGraphicalNode());
        }

        Subgraph subgraph = subgraphBuilder.build();

        return WrappedCluster.forSeed(seed, subgraph).asGraphicalCluster();
    }

    private Collection<Line> createLines(Map<AbstractError, WrappedNode> errorToNode) {
        Collection<Line> result = new HashSet<>();

        for (AbstractError error : errorToNode.keySet()) {
            WrappedNode from = errorToNode.get(error);

            for (AbstractError subError : error.getSubsequentErrors()) {
                if (errorToNode.containsKey(subError)) {
                    WrappedNode to = errorToNode.get(subError);

                    Line line = WrappedLine.forLine(from, to).asGraphicalLine();
                    result.add(line);
                }
            }
        }

        return result;
    }
}
