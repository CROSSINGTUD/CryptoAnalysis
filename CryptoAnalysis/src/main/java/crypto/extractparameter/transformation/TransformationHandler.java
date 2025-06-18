/********************************************************************************
 * Copyright (c) 2017 Fraunhofer IEM, Paderborn, Germany
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/
package crypto.extractparameter.transformation;

import boomerang.scope.AllocVal;
import boomerang.scope.Val;
import crypto.extractparameter.AllocationSiteGraph;
import crypto.extractparameter.TransformedValue;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class TransformationHandler {

    private final Collection<ITransformation> transformations;

    public TransformationHandler() {
        this.transformations =
                Set.of(
                        new StringTransformation(),
                        new OperatorTransformation(),
                        new BigIntegerTransformation(),
                        new WrapperTransformation(),
                        new MiscellaneousTransformation());
    }

    public Collection<ITransformation> getTransformations() {
        return transformations;
    }

    public Collection<TransformedValue> computeTransformedValues(AllocationSiteGraph graph) {
        Collection<TransformedValue> result = new HashSet<>();

        Val rootVal = graph.getRootVal();
        Collection<AllocVal> allocSites = graph.getAllocSites(rootVal);
        for (AllocVal allocSite : allocSites) {
            Collection<TransformedValue> transformedValues =
                    transformAllocationSite(allocSite, graph);
            result.addAll(transformedValues);
        }

        return result;
    }

    public Collection<TransformedValue> transformAllocationSite(
            AllocVal allocVal, AllocationSiteGraph graph) {
        Collection<Val> values = graph.getValues(allocVal);
        if (values.isEmpty()) {
            TransformedValue value =
                    new TransformedValue(allocVal.getAllocVal(), allocVal.getAllocStatement());

            return Collections.singleton(value);
        }

        Collection<TransformedValue> transformedValues = new HashSet<>();
        for (ITransformation transformation : transformations) {
            Collection<TransformedValue> val =
                    transformation.transformAllocationSite(allocVal, graph, this);
            transformedValues.addAll(val);
        }

        return transformedValues;
    }
}
