package com.silentmatt.dss.util;

import com.silentmatt.dss.Combinator;
import com.silentmatt.dss.Selector;
import java.util.AbstractList;
import java.util.List;

/**
 *
 * @author Matthew Crumley
 */
public class JoinedSelectorList extends AbstractList<Selector> {
    private List<Selector> parents;
    private final List<Selector> children;
    private Combinator combinator;

    public List<Selector> getParents() {
        return parents;
    }

    public void setParents(List<Selector> parents) {
        this.parents = parents;
    }

    public void setCombinator(Combinator cb) {
        combinator = cb;
    }

    public List<Selector> getChildren() {
        return children;
    }

    public JoinedSelectorList(List<Selector> parents, Combinator cb, List<Selector> children) {
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
        return children.add(value);
    }

    @Override
    public int size() {
        if (parents.isEmpty()) {
            return children.size();
        }
        return parents.size() * children.size();
    }
}
