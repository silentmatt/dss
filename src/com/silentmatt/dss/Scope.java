package com.silentmatt.dss;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * A Map from Strings to <code>T</code>s, that can inherit from other <code>Scope</scope>s.
 *
 * When storing values, the key must already exist (possibly in a parent Scope),
 * otherwise an {@link UnsupportedOperationException} will be thrown. To add an
 * entry, use the <code>declare</code> method.
 *
 * @author Matthew Crumley
 * @param <T> The type of objects being stored.
 */
public class Scope<T> implements Map<String, T> {
    /**
     * Constructs a Scope with a given parent.
     *
     * @param parent The parent Scope. <code>null</code> is allowed, and creates
     * a top-level scope.
     */
    public Scope(Scope<T> parent) {
        this.parent = parent;
        this.table = new HashMap<String, T>();
    }

    /**
     * Constructs a Scope with a given parent scope and a collection of keys to declare.
     *
     * @param scope The parent Scope.
     * @param variables A Collection<String> of keys to declare. The keys are
     *                  declared with an initial value of <code>null</code>.
     */
    public Scope(Scope<T> scope, Collection<String> variables) {
        this(scope);

        for (String name : variables) {
            declare(name);
        }
    }

    /**
     * Constructs a top-level Scope with an initial set of entries.
     *
     * @param initial A Map<String, T> of entries to pre-declare.
     */
    public Scope(Map<String, T> initial) {
        // XXX: Share the Map?
        this(null);
        table.putAll(initial);
    }

    /**
     * Gets the parent Scope.
     *
     * @return The parent Scope, or <code>null</code> if this is a top-level Scope.
     */
    public Scope<T> parent() {
        return parent;
    }

    /**
     * Returns a new scope with <code>this</code> as its parent.
     *
     * <code>scope.inherit()</code> is equivalent to <code>new Scope<T>(scope)</code>,
     * where <code>T</code> is the entry type of <code>scope</code>.
     * @return A new scope with <code>this</code> as its parent.
     */
    public Scope<T> inherit() {
        return new Scope<T>(this);
    }

    private Scope<T> parent;
    private Map<String, T> table;

    /**
     * Gets the number of entries in the Scope.
     * keys that exist in <code>this</code> and a parent Scope are only counted
     * once, i.e. <code>size()</code> returns the number of unique keys.
     *
     * @return The number of valid keys in this Scope.
     */
    public int size() {
        return keySet().size();
    }

    public boolean isEmpty() {
        return table.isEmpty() && (parent == null || parent.isEmpty());
    }

    /**
     * Returns <code>true</code> if <code>key</code> is declared in this Scope
     * or any of it's ancestors.
     *
     * <code>containsKey(key)</code> is equivalent to asking if
     * <code>put(key, value)</code> will succeed.
     *
     * @param key The key whose presence is being tested.
     * @return <code>true</code> if <code>key</code> in declared in this Scope
     * or any of it's ancestors.
     *
     * @see #declaresKey(java.lang.String)
     */
    public boolean containsKey(Object key) {
        return table.containsKey(key) || (parent != null && parent.containsKey(key));
    }

    /**
     * Returns <code>true</code> if <code>key</code> is <em>declared</em> in this Scope.
     * The parent chain is not searched.
     *
     * @param key The key whose presence is being tested.
     * @return <code>true</code> if <code>key</code> is declared in this Scope.
     *
     * @see #containsKey(java.lang.Object)
     */
    public boolean declaresKey(String key) {
        return table.containsKey(key);
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

    /**
     * Adds a key to this Scope.
     * A key must be declared in a Scope before <code>put</code> can set their
     * value. Trying to put a value in an undeclared key throws an
     * UnsupportedOperationException.
     *
     * Declaring a key that already exists in a parent scope will hide the entry
     * in the parent. Note that the {@link #size()} of the Scope will not change
     * in this case.
     *
     * @param key The key to declare.
     *
     * @see #declare(java.lang.String, java.lang.Object)
     * @see #declaresKey(java.lang.String)
     */
    public void declare(String key) {
        table.put(key, null);
    }

    /**
     * Adds a key to this Scope with an initial value.
     *
     * @param key The key to declare.
     * @param value The initial value.
     *
     * @see #declare(java.lang.String)
     * @see #declaresKey(java.lang.String)
     */
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

    /**
     * Throws an <code>UnsupportedOperationException</code>.
     * Removing entries is not allowed.
     *
     * @param key Ignored key to remove.
     * @return Does not return.
     */
    public T remove(Object key) {
        throw new UnsupportedOperationException("cannot remove a variable");
    }

    public void putAll(Map<? extends String, ? extends T> m) {
        for (Entry<? extends String, ? extends T> pair : m.entrySet()) {
            put(pair.getKey(), pair.getValue());
        }
    }

    /**
     * Throws an <code>UnsupportedOperationException</code>.
     * Clearing a Scope is not allowed.
     */
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
