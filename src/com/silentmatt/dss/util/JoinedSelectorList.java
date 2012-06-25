package com.silentmatt.dss.util;

import com.silentmatt.dss.Combinator;
import com.silentmatt.dss.Immutable;
import com.silentmatt.dss.Selector;
import java.util.AbstractList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Matthew Crumley
 */
@Immutable
public final class JoinedSelectorList extends AbstractList<Selector> {
    private final List<Selector> parents;
    private final List<Selector> children;
    private final Combinator combinator;

    public List<Selector> getParents() {
        return parents;
    }

    public List<Selector> getChildren() {
        return children;
    }

    public JoinedSelectorList(List<Selector> parents, Combinator cb, List<Selector> children) {
        this.parents = Collections.unmodifiableList(parents);
        this.children = Collections.unmodifiableList(children);
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
