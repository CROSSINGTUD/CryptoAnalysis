/********************************************************************************
 * Copyright (c) 2017 Fraunhofer IEM, Paderborn, Germany
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/
package crypto.extractparameter;

import boomerang.BackwardQuery;
import boomerang.scope.ControlFlowGraph;
import boomerang.scope.Val;
import java.util.Objects;

public class ExtractParameterQuery extends BackwardQuery {

    private final int index;
    private final String varNameInSpec;

    public ExtractParameterQuery(
            ControlFlowGraph.Edge edge, Val param, int index, String varNameInSpec) {
        super(edge, param);

        this.index = index;
        this.varNameInSpec = varNameInSpec;
    }

    public int getIndex() {
        return index;
    }

    public String getVarNameInSpec() {
        return varNameInSpec;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), index, varNameInSpec);
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj)
                && obj instanceof ExtractParameterQuery other
                && index == other.getIndex()
                && Objects.equals(varNameInSpec, other.getVarNameInSpec());
    }
}
