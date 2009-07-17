package com.silentmatt.dss;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class Scope<T> implements Map<String, T> {
    public Scope(Scope<T> parent) {
        this.parent = parent;
        this.table = new HashMap<String, T>();
    }

    Scope(Scope<T> scope, Collection<String> variables) {
        this(scope);

        for (String name : variables) {
            declare(name);
        }
    }

    // XXX: Share the Map?
    Scope(Map<String, T> initial) {
        this(null);
        table.putAll(initial);
    }

    public Scope<T> parent() {
        return parent;
    }

    public Scope<T> inherit() {
        return new Scope<T>(this);
    }

    private Scope<T> parent;
    private Map<String, T> table;

    public int size() {
        return keySet().size();
    }

    public boolean isEmpty() {
        return table.isEmpty() && (parent == null || parent.isEmpty());
    }

    public boolean containsKey(Object key) {
        return table.containsKey(key) || (parent != null && parent.containsKey(key));
    }

    public boolean containsValue(Object value) {
        if (table.containsValue(value)) {
            return true;
        }
        if (parent != null) {
            for (String key : parent.keySet()) {
                if (!table.containsKey(key)) {
                    T val = parent.get(key);
                    if (val == value || (val != null && val.equals(value))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public T get(Object key) {
        T o = table.get(key);
        if (o != null) {
            return o;
        }
        else {
            if (!table.containsKey(key) && parent != null) {
                return parent.get(key);
            }
        }
        return null;
    }

    public void declare(String key) {
        table.put(key, null);
    }

    public void declare(String key, T value) {
        table.put(key, value);
    }

    public T put(String key, T value) {
        if (table.containsKey(key)) {
            return table.put(key, value);
        }
        else if (parent != null && parent.containsKey(key)) {
            return parent.put(key, value);
        }

        throw new UnsupportedOperationException("undefined variable");
    }

    public T remove(Object key) {
        throw new UnsupportedOperationException("cannot remove a variable");
    }

    public void putAll(Map<? extends String, ? extends T> m) {
        for (Entry<? extends String, ? extends T> pair : m.entrySet()) {
            put(pair.getKey(), pair.getValue());
        }
    }

    public void clear() {
        throw new UnsupportedOperationException("cannot clear a scope");
    }

    public Set<String> keySet() {
        Set<String> keys = table.keySet();
        if (parent != null) {
            keys = new HashSet<String>(keys);
            keys.addAll(parent.keySet());
        }
        return keys;
    }

    public Collection<T> values() {
        LinkedList<T> result = new LinkedList<T>();
        for (String name : keySet()) {
            result.add(get(name));
        }
        return result;
    }

    public Set<Entry<String, T>> entrySet() {
        Set<Entry<String, T>> entries = table.entrySet();

        if (parent != null) {
            entries = new java.util.HashSet<Entry<String, T>>(entries);
            for (Entry<String, T> entry : parent.entrySet()) {
                if (!table.containsKey(entry.getKey())) {
                    entries.add(entry);
                }
            }
        }

        return entries;
    }
}
