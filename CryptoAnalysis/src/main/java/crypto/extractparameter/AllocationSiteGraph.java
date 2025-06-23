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

import boomerang.scope.AllocVal;
import boomerang.scope.Val;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.util.Collection;

public class AllocationSiteGraph {

    private final Val rootVal;
    private final Multimap<Val, AllocVal> valToAllocSites;
    private final Multimap<AllocVal, Val> allocSiteToValues;

    public AllocationSiteGraph(Val rootVal) {
        this.rootVal = rootVal;

        this.valToAllocSites = HashMultimap.create();
        this.allocSiteToValues = HashMultimap.create();
    }

    public Val getRootVal() {
        return rootVal;
    }

    public void addValToAllocSiteEdge(Val val, Collection<AllocVal> allocSite) {
        if (val instanceof AllocVal) {
            throw new IllegalArgumentException("Val should not be an AllocVal");
        }

        valToAllocSites.putAll(val, allocSite);
    }

    public void addAllocSiteToValEdge(AllocVal allocSite, Collection<Val> val) {
        allocSiteToValues.putAll(allocSite, val);
    }

    public Collection<AllocVal> getAllocSites(Val val) {
        return valToAllocSites.get(val);
    }

    public Collection<Val> getValues(AllocVal allocVal) {
        return allocSiteToValues.get(allocVal);
    }
}
