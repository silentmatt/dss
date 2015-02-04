package com.silentmatt.dss.declaration;

import com.google.common.collect.ImmutableList;
import com.silentmatt.dss.Immutable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

/**
 * A list of {@link Declaration}s that also acts like a {@link Map}.
 *
 * If you need an actual List or Map, call {@link #asMap()} to get a Map view.
 *
 * @author Matthew Crumley
 */
@Immutable
public final class DeclarationList implements Iterable<Declaration> {
    private final ImmutableList<Declaration> list;

    public static final DeclarationList EMPTY = new DeclarationList(ImmutableList.copyOf(new Declaration[0]));

    /**
     * Constructs a DeclarationList containing the Declarations from the specified list.
     *
     * @param declarations {@link List} of {@link Declaration}s to copy.
     */
    public DeclarationList(ImmutableList<Declaration> declarations) {
        list = declarations;
    }

    public ImmutableList<Declaration> toList() {
        return list;
    }

    public int size() {
        return list.size();
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public boolean contains(Object arg0) {
        return arg0 instanceof Declaration && list.contains((Declaration)arg0);
    }

    @Override
    public Iterator<Declaration> iterator() {
        return list.iterator();
    }

    public boolean containsAll(Collection<?> arg0) {
        return list.containsAll(arg0);
    }

    public Declaration get(int arg0) {
        return list.get(arg0);
    }

    public int indexOf(Object arg0) {
        return list.indexOf(arg0);
    }

    public int lastIndexOf(Object arg0) {
        return list.lastIndexOf(arg0);
    }

    public ListIterator<Declaration> listIterator() {
        return list.listIterator();
    }

    public ListIterator<Declaration> listIterator(int start) {
        return list.listIterator(start);
    }

    // Map methods
    public boolean containsKey(String key) {
        for (Declaration declaration : list) {
            if (matches(declaration, key)) {
                return true;
            }
        }
        return false;
    }

    public boolean containsValue(Expression value) {
        for (Declaration declaration : list) {
            if (declaration.getExpression().equals(value)) {
                return true;
            }
        }
        return false;
    }

    private static boolean matches(Declaration declaration, String name) {
        return declaration.getName().equalsIgnoreCase(name);
    }

    @Deprecated
    public ImmutableList<Declaration> getAllDeclarations(String name) {
        ImmutableList.Builder<Declaration> all = ImmutableList.builder();

        for (Declaration declaration : list) {
            if (matches(declaration, name)) {
                all.add(declaration);
            }
        }

        return all.build();
    }

    public Declaration getDeclaration(String name) {
        Declaration found = null;

        ListIterator<Declaration> it = list.listIterator(list.size());
        while (it.hasPrevious()) {
            Declaration declaration = it.previous();
            if (matches(declaration, name)) {
                if (declaration.isImportant()) {
                    found = declaration;
                    break;
                }
                else if (found == null) {
                    found = declaration;
                }
            }
        }

        return found;
    }

    public Expression get(String name) {
        Declaration declaration = getDeclaration(name);
        return declaration != null ? declaration.getExpression() : null;
    }
}
