package com.silentmatt.dss.evaluator;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A Map from Strings to <code>T</code>s, that can inherit from other <code>Scope</code>s.
 *
 * @author Matthew Crumley
 * @param <T> The type of objects being stored.
 */
public class Scope<T> {
    /**
     * Constructs a Scope with a given parent.
     *
     * @param parent The parent Scope. <code>null</code> is allowed, and creates
     * a top-level scope.
     */
    public Scope(Scope<T> parent) {
        this.parentScope = parent;
        this.table = new HashMap<>();
    }

    /**
     * Constructs a Scope with a given parent scope and a collection of keys to declare.
     *
     * @param scope The parent Scope.
     * @param variables A Collection of keys to declare. The keys are declared
     *                  with an initial value of <code>null</code>.
     */
    public Scope(Scope<T> scope, Collection<String> variables) {
        this(scope);

        for (String name : variables) {
            table.put(name, null);
        }
    }

    /**
     * Creates a Scope with an initial set of entries.
     *
     * @param parent The parent Scope.
     * @param initial A Map<String, T> of entries to pre-declare.
     */
    public Scope(Scope<T> parent, Map<String, T> initial) {
        this(parent);
        table.putAll(initial);
    }

    @Override
    public final String toString() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, T> e : entrySet()) {
            sb.append(e.getKey()).append(": ").append(e.getValue()).append("\n");
        }
        return sb.toString();
    }

    /**
     * Gets the parent Scope.
     *
     * @return The parent Scope, or <code>null</code> if this is a top-level Scope.
     */
    public Scope<T> parent() {
        return parentScope;
    }

    /**
     * Gets the global Scope.
     *
     * @return The global Scope, or <code>this</code> if this is a top-level Scope.
     */
    public Scope<T> getGlobalScope() {
        Scope<T> scope = this;
        while (scope.parent() != null) {
            scope = scope.parent();
        }
        return scope;
    }

    /**
     * Returns a new scope with <code>this</code> as its parent.
     *
     * <code>scope.inherit()</code> is equivalent to <code>new Scope&lt;T&gt;(scope)</code>,
     * where <code>T</code> is the entry type of <code>scope</code>.
     * @return A new scope with <code>this</code> as its parent.
     */
    public final Scope<T> inherit() {
        return new Scope<>(this);
    }

    protected final Scope<T> parentScope;
    protected final Map<String, T> table;

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
    public final boolean containsKey(String key) {
        return table.containsKey(key) || (parentScope != null && parentScope.containsKey(key));
    }

    /**
     * Returns <code>true</code> if <code>key</code> is <em>declared</em> in this Scope.
     * The parent chain is not searched.
     *
     * @param key The key whose presence is being tested.
     * @return <code>true</code> if <code>key</code> is declared in this Scope.
     *
     * @see #containsKey(java.lang.String)
     */
    public boolean declaresKey(String key) {
        return table.containsKey(key);
    }

    public final T get(String key) {
        T value = table.get(key);
        if (value != null) {
            return value;
        }
        else if (!table.containsKey(key) && parentScope != null) {
            return parentScope.get(key);
        }

        return null;
    }

    /**
     * Adds a key to this Scope with an initial value.
     *
     * @param key The key to declare.
     * @param value The initial value.
     *
     * @see #declaresKey(java.lang.String)
     */
    public final void declare(String key, T value) {
        table.put(key, value);
    }

    @Deprecated
    private Set<Map.Entry<String, T>> entrySet() {
        Set<Map.Entry<String, T>> entries = table.entrySet();

        if (parentScope != null) {
            entries = new java.util.HashSet<>(entries);
            for (Map.Entry<String, T> entry : parentScope.entrySet()) {
                if (!table.containsKey(entry.getKey())) {
                    entries.add(entry);
                }
            }
        }

        return entries;
    }
}
