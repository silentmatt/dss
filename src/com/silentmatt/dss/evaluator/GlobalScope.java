package com.silentmatt.dss.evaluator;

import java.util.Collection;

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
public class GlobalScope<T> extends Scope<T> {
    /**
     * Constructs a Scope with a given parent.
     *
     * @param parent The parent Scope. <code>null</code> is allowed, and creates
     * a top-level scope.
     */
    public GlobalScope(Scope<T> parent) {
    	super(parent);
    }

    /**
     * Constructs a Scope with a given parent scope and a collection of keys to declare.
     *
     * @param scope The parent Scope.
     * @param variables A Collection<String> of keys to declare. The keys are
     *                  declared with an initial value of <code>null</code>.
     */
    public GlobalScope(Scope<T> scope, Collection<String> variables) {
        super(scope, variables);
    }

    /**
     * Gets the parent Scope.
     *
     * @return The parent Scope, or <code>null</code> if this is a top-level Scope.
     */
    @Override
    public Scope<T> parent() {
        return null;
    }

    /**
     * Returns <code>true</code> if <code>key</code> is <em>declared</em> in this Scope.
     * The parent chain <em>is</em> searched.
     *
     * @param key The key whose presence is being tested.
     * @return <code>true</code> if <code>key</code> is declared in this Scope.
     *
     * @see #containsKey(java.lang.Object)
     */
    @Override
    public boolean declaresKey(String key) {
        return containsKey(key);
    }
}
