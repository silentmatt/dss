package com.silentmatt.dss.util;

import java.util.AbstractList;
import java.util.List;

/**
 *
 * @author Matthew Crumley
 */
public class JoinedList<E> extends AbstractList<E> {
    private final List<E> front;
    private final List<E> back;

    public List<E> getFront() {
        return front;
    }

    public List<E> getBack() {
        return back;
    }

    public JoinedList(List<E> front, List<E> back) {
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
        if (index < front.size()) {
            return front.set(index, value);
        }
        return back.set(index - front.size(), value);
    }

    @Override
    public void add(int index, E value) {
        if (index < front.size()) {
            front.add(index, value);
        }
        else {
            back.add(index - front.size(), value);
        }
    }

    @Override
    public int size() {
        return front.size() + back.size();
    }
}
