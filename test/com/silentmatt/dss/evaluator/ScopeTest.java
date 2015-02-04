package com.silentmatt.dss.evaluator;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class ScopeTest {
    Scope<Integer> globalScope;
    Scope<Integer> parentScope;
    Scope<Integer> localScope;

    @Before
    public void setUp() {
        globalScope = new Scope<>(null, Collections.singletonMap("fromGlobal", 42));
        parentScope = new Scope<>(globalScope, Collections.singletonMap("fromParent", 21));
        localScope = new Scope<>(parentScope, Collections.singletonMap("fromLocal", 5));
    }

    @Test
    public void testConstructors() {
        Scope<Integer> parent = new Scope<>(null);
        parent.declare("fromParent", 99);

        localScope = new Scope<>(parent);
        assertEquals(Integer.valueOf(99), localScope.get("fromParent"));

        localScope = new Scope<>(parent, Arrays.asList("a", "b"));
        assertTrue(localScope.declaresKey("a"));
        assertTrue(localScope.declaresKey("b"));
        assertFalse(parent.containsKey("a"));
        assertFalse(parent.containsKey("b"));

        Map<String, Integer> variables = new HashMap<>();
        variables.put("A", 1);
        variables.put("B", 2);
        localScope = new Scope<>(parent, variables);
        assertEquals(Integer.valueOf(1), localScope.get("A"));
        assertEquals(Integer.valueOf(2), localScope.get("B"));
        assertEquals(Integer.valueOf(99), localScope.get("fromParent"));
        assertFalse(parent.containsKey("A"));
    }

    @Test
    public void parentScopes() {
        assertSame(parentScope, localScope.parent());
        assertSame(globalScope, localScope.parent().parent());
        assertNull(globalScope.parent());
        assertSame(globalScope, localScope.getGlobalScope());
        assertSame(globalScope, globalScope.getGlobalScope());
        
    }

    @Test
    public void ownAndParentValuesAreAccessible() {
        assertEquals(Integer.valueOf(5), localScope.get("fromLocal"));
        assertEquals(Integer.valueOf(21), localScope.get("fromParent"));
        assertEquals(Integer.valueOf(42), localScope.get("fromGlobal"));
    }

    @Test
    public void declaresKeyExcludesParents() {
        assertTrue(localScope.declaresKey("fromLocal"));
        assertFalse(localScope.declaresKey("fromParent"));
        assertFalse(localScope.declaresKey("fromGlobal"));

        assertTrue(parentScope.declaresKey("fromParent"));
        assertFalse(parentScope.declaresKey("fromGlobal"));

        assertTrue(globalScope.declaresKey("fromGlobal"));
    }

    @Test
    public void containsKeyIncludesParents() {
        assertTrue(localScope.containsKey("fromLocal"));
        assertTrue(localScope.containsKey("fromParent"));
        assertTrue(localScope.containsKey("fromGlobal"));
    }
}
