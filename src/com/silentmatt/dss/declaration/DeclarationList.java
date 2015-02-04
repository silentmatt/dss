package com.silentmatt.dss.declaration;

import com.google.common.collect.ImmutableList;
import com.silentmatt.dss.Immutable;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * A list of {@link Declaration}s that also acts like a {@link Map}.
 *
 * All of the methods from {@link Map} that are compatible with the {@link List}
 * interface can be used on a DeclarationList. If you need an actual Map, call
 * {@link #asMap()} to get a Map-view of the list.
 *
 * @author Matthew Crumley
 */
@Immutable
public final class DeclarationList implements /*List<Declaration>*/ Iterable<Declaration> {
    private final ImmutableList<Declaration> list;
    private final Map<String, Expression> mapView = new DeclarationListMapView();

    //@SuppressWarnings("unchecked")
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

    public Object[] toArray() {
        return list.toArray();
    }

    public <T> T[] toArray(T[] arg0) {
        return list.toArray(arg0);
    }

//    public boolean add(Declaration arg0) {
//        throw new UnsupportedOperationException();
//        //return list.add(arg0);
//    }
//
//    public boolean remove(Object arg0) {
//        throw new UnsupportedOperationException();
//        //return list.remove(arg0);
//    }

    public boolean containsAll(Collection<?> arg0) {
        return list.containsAll(arg0);
    }

//    public boolean addAll(Collection<? extends Declaration> arg0) {
//        throw new UnsupportedOperationException();
//        //return list.addAll(arg0);
//    }
//
//    public boolean addAll(int arg0, Collection<? extends Declaration> arg1) {
//        throw new UnsupportedOperationException();
//        //return list.addAll(arg0, arg1);
//    }
//
//    public boolean removeAll(Collection<?> arg0) {
//        throw new UnsupportedOperationException();
//        //return list.removeAll(arg0);
//    }
//
//    public boolean retainAll(Collection<?> arg0) {
//        throw new UnsupportedOperationException();
//        //return list.retainAll(arg0);
//    }
//
//    public void clear() {
//        throw new UnsupportedOperationException();
//        //list.clear();
//    }

    public Declaration get(int arg0) {
        return list.get(arg0);
    }

//    public Declaration set(int arg0, Declaration arg1) {
//        throw new UnsupportedOperationException();
//        //return list.set(arg0, arg1);
//    }
//
//    public void add(int arg0, Declaration arg1) {
//        throw new UnsupportedOperationException();
//        //list.add(arg0, arg1);
//    }
//
//    public Declaration remove(int arg0) {
//        throw new UnsupportedOperationException();
//        //return list.remove(arg0);
//    }

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

//    public List<Declaration> subList(int start, int end) {
//        throw new UnsupportedOperationException();
//        //return list.subList(start, end);
//    }

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

//    public Expression put(String name, Expression expression) {
//        throw new UnsupportedOperationException();
////        Expression result = get(name);
////        list.add(new Declaration(name, expression));
////        return result;
//    }

//    public Expression remove(String key) {
//        throw new UnsupportedOperationException();
////        Expression result = null;
////
////        ListIterator<Declaration> it = list.listIterator();
////        while (it.hasNext()) {
////            Declaration current = it.next();
////            if (matches(current, key)) {
////                result = current.getExpression();
////                it.remove();
////            }
////        }
////
////        return result;
//    }

//    public void putAll(Map<? extends String, ? extends Expression> map) {
//        throw new UnsupportedOperationException();
////        for (Entry<? extends String, ? extends Expression> entry : map.entrySet()) {
////            this.put(entry.getKey(), entry.getValue());
////        }
//    }

    /**
     * Gets a view of the list that implements the {@link Map} interface}.
     * @return
     */
    public Map<String, Expression> asMap() {
        return this.mapView;
    }

    private class DeclarationListMapView implements Map<String, Expression> {
        @Override
        public int size() {
            return DeclarationList.this.size();
        }

        @Override
        public boolean isEmpty() {
            return DeclarationList.this.isEmpty();
        }

        @Override
        public boolean containsKey(Object arg0) {
            return arg0 instanceof String && DeclarationList.this.containsKey((String) arg0);
        }

        @Override
        public boolean containsValue(Object arg0) {
            return arg0 instanceof Expression && DeclarationList.this.containsValue((Expression) arg0);
        }

        @Override
        public Expression get(Object arg0) {
            return arg0 instanceof String ? DeclarationList.this.get((String) arg0) : null;
        }

        @Override
        public Expression put(String arg0, Expression arg1) {
            throw new UnsupportedOperationException();
            //return DeclarationList.this.put(arg0, arg1);
        }

        @Override
        public Expression remove(Object arg0) {
            throw new UnsupportedOperationException();
//            if (arg0 instanceof String) {
//                return DeclarationList.this.remove((String) arg0);
//            }
//            return null;
        }

        @Override
        public void putAll(Map<? extends String, ? extends Expression> arg0) {
            throw new UnsupportedOperationException();
            //DeclarationList.this.putAll(arg0);
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
            //DeclarationList.this.clear();
        }

        @Override
        public Set<String> keySet() {
            return new DeclarationListKeySet();
        }

        @Override
        public Collection<Expression> values() {
            return new DeclarationListValues();
        }

        @Override
        public Set<Entry<String, Expression>> entrySet() {
            return new DeclarationListEntrySet();
        }

        private class DeclarationListKeySet implements Set<String> {
            private final Set<String> keys = new LinkedHashSet<>();

            public DeclarationListKeySet() {
                for (Declaration declaration : DeclarationList.this.list) {
                    keys.add(declaration.getName());
                }
            }

            @Override
            public int size() {
                return keys.size();
            }

            @Override
            public boolean isEmpty() {
                return keys.isEmpty();
            }

            @Override
            public boolean contains(Object arg0) {
                return keys.contains(arg0);
            }

            @Override
            public Iterator<String> iterator() {
                return new Iterator<String>() {
                    final Iterator<String> it = keys.iterator();

                    @Override
                    public boolean hasNext() {
                        return it.hasNext();
                    }

                    @Override
                    public String next() {
                        return it.next();
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
//                        keys.remove(current);
//                        DeclarationList.this.remove(current);
                    }
                };
            }

            @Override
            public Object[] toArray() {
                return keys.toArray();
            }

            @Override
            public <T> T[] toArray(T[] arg0) {
                return keys.toArray(arg0);
            }

            @Override
            public boolean add(String arg0) {
                throw new UnsupportedOperationException("Cannot call add to a key set.");
            }

            @Override
            public boolean remove(Object arg0) {
                throw new UnsupportedOperationException();
//                if (!(arg0 instanceof String)) {
//                    return false;
//                }
//                String key = (String) arg0;
//                DeclarationList.this.remove(key);
//                return keys.remove(key);
            }

            @Override
            public boolean containsAll(Collection<?> arg0) {
                return keys.containsAll(arg0);
            }

            @Override
            public boolean addAll(Collection<? extends String> arg0) {
                throw new UnsupportedOperationException("Cannot call addAll on a key set.");
            }

            @Override
            public boolean retainAll(Collection<?> arg0) {
                //keys.retainAll(arg0);
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean removeAll(Collection<?> arg0) {
                throw new UnsupportedOperationException();
//                boolean result = false;
//                for (Object o : arg0) {
//                    result |= remove(o);
//                }
//                return result;
            }

            @Override
            public void clear() {
                throw new UnsupportedOperationException();
//                keys.clear();
//                DeclarationList.this.clear();
            }
        }

        private class DeclarationListEntrySet implements Set<Entry<String, Expression>> {
            @Override
            public int size() {
                return DeclarationList.this.size();
            }

            @Override
            public boolean isEmpty() {
                return DeclarationList.this.isEmpty();
            }

            @Override
            public boolean contains(Object arg0) {
                return arg0 instanceof Declaration && DeclarationList.this.contains((Declaration)arg0);
            }

            @Override
            public Iterator<Entry<String, Expression>> iterator() {
                return new Iterator<Entry<String, Expression>>() {
                    final Iterator<Declaration> it = DeclarationList.this.iterator();
                    @Override
                    public boolean hasNext() {
                        return it.hasNext();
                    }

                    @Override
                    public Entry<String, Expression> next() {
                        return it.next();
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                        //it.remove();
                    }
                };
            }

            @Override
            public Object[] toArray() {
                return DeclarationList.this.toArray();
            }

            @Override
            public <T> T[] toArray(T[] arg0) {
                return DeclarationList.this.toArray(arg0);
            }

            @Override
            public boolean add(Entry<String, Expression> arg0) {
                throw new UnsupportedOperationException("Cannot call add on an entry set.");
            }

            @Override
            public boolean remove(Object arg0) {
                throw new UnsupportedOperationException();
                //return DeclarationList.this.remove(arg0);
            }

            @Override
            public boolean containsAll(Collection<?> arg0) {
                return DeclarationList.this.containsAll(arg0);
            }

            @Override
            public boolean addAll(Collection<? extends Entry<String, Expression>> arg0) {
                throw new UnsupportedOperationException("Cannot call addAll on an entry set.");
            }

            @Override
            public boolean retainAll(Collection<?> arg0) {
                throw new UnsupportedOperationException();
                //return DeclarationList.this.retainAll(arg0);
            }

            @Override
            public boolean removeAll(Collection<?> arg0) {
                throw new UnsupportedOperationException();
                //return DeclarationList.this.removeAll(arg0);
            }

            @Override
            public void clear() {
                throw new UnsupportedOperationException();
                //DeclarationList.this.clear();
            }
        }

        private class DeclarationListValues implements Collection<Expression> {
            @Override
            public int size() {
                return DeclarationList.this.size();
            }

            @Override
            public boolean isEmpty() {
                return DeclarationList.this.isEmpty();
            }

            @Override
            public boolean contains(Object arg0) {
                return arg0 instanceof Expression && DeclarationList.this.containsValue((Expression) arg0);
            }

            @Override
            public Iterator<Expression> iterator() {
                return new Iterator<Expression>() {
                    Iterator<Declaration> it = DeclarationList.this.iterator();
                    @Override
                    public boolean hasNext() {
                        return it.hasNext();
                    }

                    @Override
                    public Expression next() {
                        return it.next().getExpression();
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                        //it.remove();
                    }
                };
            }

            @Override
            public Object[] toArray() {
                return toArray(new Object[size()]);
            }

            @SuppressWarnings("unchecked")
            @Override
            public <T> T[] toArray(T[] arg0) {
                if (arg0.length >= size()) {
                    int i = 0;
                    for (Declaration declaration : DeclarationList.this) {
                       arg0[i++] = (T) declaration;
                    }
                    if (i < size()) {
                        arg0[i] = null;
                    }
                    return arg0;
                }

                T[] array = (T[]) Array.newInstance(arg0.getClass().getComponentType(), size());
                int i = 0;
                for (Object declaration : DeclarationList.this) {
                    try {
                        array[i++] = (T) declaration;
                    }
                    catch (ClassCastException ex) {
                        throw new ArrayStoreException();
                    }
                }
                return array;
            }

            @Override
            public boolean add(Expression arg0) {
                throw new UnsupportedOperationException("Cannot call add on a value set.");
            }

            @Override
            public boolean remove(Object arg0) {
                throw new UnsupportedOperationException();
//                boolean result = false;
//
//                Iterator<Declaration> it = DeclarationList.this.iterator();
//                while (it.hasNext()) {
//                    Declaration declaration = it.next();
//                    if (declaration.getExpression().equals(arg0)) {
//                        result = true;
//                        it.remove();
//                    }
//                }
//
//                return result;
            }

            @Override
            public boolean containsAll(Collection<?> arg0) {
                for (Object o : arg0) {
                    if (!(o instanceof Expression && contains((Expression)o))) {
                        return false;
                    }
                }
                return true;
            }

            @Override
            public boolean addAll(Collection<? extends Expression> arg0) {
                throw new UnsupportedOperationException("Cannot call addAll on a value set.");
            }

            @Override
            public boolean removeAll(Collection<?> arg0) {
                throw new UnsupportedOperationException();
//                boolean result = false;
//                for (Object o : arg0) {
//                    result |= remove(o);
//                }
//                return result;
            }

            @Override
            public boolean retainAll(Collection<?> arg0) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void clear() {
                throw new UnsupportedOperationException();
                //DeclarationList.this.clear();
            }
        }
    }
}