package com.silentmatt.dss.evaluator;

import java.util.Collection;
import java.util.Map;

/**
 * A {@link Scope} that acts as a top-level scope with or without a parent.
 * 
 * A <code>GlobalScope</code> hides it's parent/ancestors, but still gives access to
 * their values, essentially treating itself and its ancestors as a single scope.
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
     * @param variables A Collection of keys to declare. The keys are declared
     *                  with an initial value of <code>null</code>.
     */
    public GlobalScope(Scope<T> scope, Collection<String> variables) {
        super(scope, variables);
    }

    /**
     * Creates a Scope with an initial set of entries.
     *
     * @param parent The parent Scope.
     * @param initial A Map<String, T> of entries to pre-declare.
     */
    public GlobalScope(Scope<T> parent, Map<String, T> initial) {
        super(parent, initial);
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
     * Gets the global Scope.
     *
     * @return <code>this</code>.
     */
    @Override
    public GlobalScope<T> getGlobalScope() {
        return this;
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
