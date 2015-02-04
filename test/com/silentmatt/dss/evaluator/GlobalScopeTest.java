package com.silentmatt.dss.evaluator;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class GlobalScopeTest {
    Scope<Integer> superSuperGlobal;
    Scope<Integer> superGlobal;
    GlobalScope<Integer> globalScope;

    @Before
    public void setUp() {
        superSuperGlobal = new Scope<>(null, Collections.singletonMap("fromSuperSuperGlobal", 42));
        superGlobal = new Scope<>(superSuperGlobal, Collections.singletonMap("fromSuperGlobal", 21));
        globalScope = new GlobalScope<>(superGlobal, Collections.singletonMap("fromGlobal", 5));
    }

    @Test
    public void testConstructors() {
        Scope<Integer> parent = new Scope<>(null);
        parent.declare("fromParent", 99);

        globalScope = new GlobalScope<>(parent);
        assertEquals(Integer.valueOf(99), globalScope.get("fromParent"));

        globalScope = new GlobalScope<>(parent, Arrays.asList("a", "b"));
        assertTrue(globalScope.declaresKey("a"));
        assertTrue(globalScope.declaresKey("b"));
        assertFalse(parent.containsKey("a"));
        assertFalse(parent.containsKey("b"));

        Map<String, Integer> variables = new HashMap<>();
        variables.put("A", 1);
        variables.put("B", 2);
        globalScope = new GlobalScope<>(parent, variables);
        assertEquals(Integer.valueOf(1), globalScope.get("A"));
        assertEquals(Integer.valueOf(2), globalScope.get("B"));
        assertEquals(Integer.valueOf(99), globalScope.get("fromParent"));
        assertFalse(parent.containsKey("A"));
    }

    @Test
    public void actsAsTopLevelScope() {
        assertNull(globalScope.parent());
        assertSame(globalScope, globalScope.getGlobalScope());
    }

    @Test
    public void ownAndParentValuesAreAccessible() {
        assertEquals(Integer.valueOf(5), globalScope.get("fromGlobal"));
        assertEquals(Integer.valueOf(21), globalScope.get("fromSuperGlobal"));
        assertEquals(Integer.valueOf(42), globalScope.get("fromSuperSuperGlobal"));
    }

    @Test
    public void declaresKeyChecksParents() {
        assertTrue(globalScope.declaresKey("fromGlobal"));
        assertTrue(globalScope.declaresKey("fromSuperGlobal"));
        assertTrue(globalScope.declaresKey("fromSuperSuperGlobal"));
    }
}
