package com.silentmatt.dss.util;

import com.google.common.collect.ImmutableList;
import com.silentmatt.dss.Immutable;
import com.silentmatt.dss.selector.Combinator;
import com.silentmatt.dss.selector.Selector;
import java.util.AbstractList;
import java.util.List;

/**
 *
 * @author Matthew Crumley
 */
@Immutable
public final class JoinedSelectorList extends AbstractList<Selector> {
    private final ImmutableList<Selector> parents;
    private final ImmutableList<Selector> children;
    private final Combinator combinator;

    public List<Selector> getParents() {
        return parents;
    }

    public List<Selector> getChildren() {
        return children;
    }

    public JoinedSelectorList(ImmutableList<Selector> parents, Combinator cb, ImmutableList<Selector> children) {
        this.parents = parents;
        this.children = children;
        this.combinator = cb;
    }

    @Override
    public Selector get(int index) {
        int childrenSize = children.size();
        int pIndex = index / childrenSize;
        int cIndex = index % childrenSize;

        if (parents.isEmpty()) {
            return children.get(cIndex);
        }
        return new Selector(parents.get(pIndex), combinator, children.get(cIndex));
    }

    @Override
    public void add(int index, Selector value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean add(Selector value) {
        throw new UnsupportedOperationException();
        //return children.add(value);
    }

    @Override
    public int size() {
        if (parents.isEmpty()) {
            return children.size();
        }
        return parents.size() * children.size();
    }
}
