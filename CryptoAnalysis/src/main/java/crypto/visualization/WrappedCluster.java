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
import java.util.Objects;
import org.graphper.api.Cluster;
import org.graphper.api.Subgraph;
import org.graphper.api.attributes.ClusterShapeEnum;
import org.graphper.api.attributes.Color;
import org.graphper.api.attributes.Labeljust;

public class WrappedCluster {

    private final IAnalysisSeed seed;
    private final Subgraph subgraph;
    private Cluster cluster;

    private WrappedCluster(IAnalysisSeed seed, Subgraph subgraph) {
        this.seed = seed;
        this.subgraph = subgraph;
    }

    public static WrappedCluster forSeed(IAnalysisSeed seed, Subgraph subgraph) {
        return new WrappedCluster(seed, subgraph);
    }

    public Cluster asGraphicalCluster() {
        if (cluster == null) {
            cluster =
                    Cluster.builder()
                            .subgraph(subgraph)
                            .label(" " + seed.getFact().getVariableName())
                            .labeljust(Labeljust.LEFT)
                            .shape(ClusterShapeEnum.RECT)
                            .bgColor(Color.ofRGB("#E8E8E8")) // Gray-ish
                            .build();
        }

        return cluster;
    }

    @Override
    public int hashCode() {
        return Objects.hash(seed);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof WrappedCluster other && Objects.equals(seed, other.seed);
    }

    @Override
    public String toString() {
        return "Cluster: " + seed;
    }
}
