package com.silentmatt.dss.util;

import com.google.common.collect.ImmutableList;
import com.silentmatt.dss.Immutable;
import java.util.AbstractList;

/**
 *
 * @author Matthew Crumley
 */
@Immutable
public final class JoinedList<E> extends AbstractList<E> {
    private final ImmutableList<E> front;
    private final ImmutableList<E> back;

    public JoinedList(ImmutableList<E> front, ImmutableList<E> back) {
        this.front = front;
        this.back = back;
    }

    @Override
    public E get(int index) {
        if (index < front.size()) {
            return front.get(index);
        }
        return back.get(index - front.size());
    }

    @Override
    public E set(int index, E value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(int index, E value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int size() {
        return front.size() + back.size();
    }
}
