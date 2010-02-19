package com.silentmatt.dss;

import com.silentmatt.dss.css.CssDeclaration;
import com.silentmatt.dss.directive.ClassDirective;
import com.silentmatt.dss.term.ClassReferenceTerm;
import com.silentmatt.dss.term.Term;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 *
 * @author Matthew Crumley
 */
public class DeclarationList implements List<Declaration> {
    private List<Declaration> list = new ArrayList<Declaration>();
    private final Map<String, Expression> mapView = new DeclarationListMapView();

    public DeclarationList() {
    }

    public DeclarationList(List<Declaration> declarations) {
        list.addAll(declarations);
    }

    public int size() {
        return list.size();
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public boolean contains(Object arg0) {
        return list.contains(arg0);
    }

    public Iterator<Declaration> iterator() {
        return list.iterator();
    }

    public Object[] toArray() {
        return list.toArray();
    }

    public <T> T[] toArray(T[] arg0) {
        return list.toArray(arg0);
    }

    public boolean add(Declaration arg0) {
        return list.add(arg0);
    }

    public boolean remove(Object arg0) {
        return list.remove(arg0);
    }

    public boolean containsAll(Collection<?> arg0) {
        return list.containsAll(arg0);
    }

    public boolean addAll(Collection<? extends Declaration> arg0) {
        return list.addAll(arg0);
    }

    public boolean addAll(int arg0, Collection<? extends Declaration> arg1) {
        return list.addAll(arg0, arg1);
    }

    public boolean removeAll(Collection<?> arg0) {
        return list.removeAll(arg0);
    }

    public boolean retainAll(Collection<?> arg0) {
        return list.retainAll(arg0);
    }

    public void clear() {
        list.clear();
    }

    public Declaration get(int arg0) {
        return list.get(arg0);
    }

    public Declaration set(int arg0, Declaration arg1) {
        return list.set(arg0, arg1);
    }

    public void add(int arg0, Declaration arg1) {
        list.add(arg0, arg1);
    }

    public Declaration remove(int arg0) {
        return list.remove(arg0);
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

    public List<Declaration> subList(int start, int end) {
        return list.subList(start, end);
    }

    public void inheritProperties(DeclarationList properties) {
        for (int i = 0; i < properties.size(); i++) {
            Declaration declaration = properties.get(i);
            add(declaration);
        }
    }

    private void addInheritedProperties(EvaluationState state, ClassReferenceTerm classReference) throws IOException {
        String className = classReference.getName();
        ClassDirective clazz = state.getClasses().get(className);
        if (clazz == null) {
            state.getErrors().SemErr("no such class: " + className);
            return;
        }

        // Make a copy of the properties, to substitute parameters into
        DeclarationList properties = new DeclarationList();
        for (Declaration prop : clazz.getDeclarations(classReference.getArguments())) {
            properties.add(new Declaration(prop.getName(), prop.getExpression(), prop.isImportant()));
        }

        DeclarationList formalParameters = clazz.getParameters(classReference.getArguments());

        // Fill in the parameter values
        state.pushParameters();
        try {
            // Defaults
            for (Declaration param : formalParameters) {
                state.getParameters().declare(param.getName(), param.getExpression());
            }
            // Arguments
            int argNumber = 0;
            for (Declaration arg : classReference.getArguments()) {
                String paramName = arg.getName();
                if (paramName.isEmpty()) {
                    if (argNumber < 0) {
                        state.getErrors().SemErr("Positional arguments cannot follow named arguments in " + className);
                        return;
                    }
                    if (argNumber < formalParameters.size()) {
                        paramName = formalParameters.get(argNumber).getName();
                    }
                    else {
                        state.getErrors().Warning("Too many arguments to class '" + className + "'");
                        continue;
                    }
                    ++argNumber;
                }
                else {
                    argNumber = -1;
                }

                if (state.getParameters().declaresKey(paramName)) {
                    state.getParameters().put(paramName, arg.getExpression());
                }
                else {
                    state.getErrors().Warning(className + " does not have a parameter '" + arg.getName() + "'");
                }
            }

            for (Declaration dec : properties) {
                dec.substituteValue(state, this, true, true);
            }
        }
        finally {
            state.popParameters();
        }

        inheritProperties(properties);
    }

    private void addInheritedProperties(EvaluationState state, Expression inherits) throws IOException {
        for (Term inherit : inherits.getTerms()) {
            if (inherit instanceof ClassReferenceTerm) {
                addInheritedProperties(state, (ClassReferenceTerm) inherit);
            }
            else {
                // XXX: May want to split ClassReferenceTerm into SimpleCRT and ParameterizedCRT
                // so this doesn't need to create a new arguments list every time (SCRT would share one)
                addInheritedProperties(state, new ClassReferenceTerm(inherit.toString()));
            }
        }
    }

    // FIXME: this modifies this.list
    public List<CssDeclaration> evaluateStyle(EvaluationState state, boolean doCalculations) throws IOException {
        List<CssDeclaration> result = new ArrayList<CssDeclaration>();

        state.pushScope();
        try {
            DeclarationList newList = new DeclarationList();
            for (Declaration declaration : list) {
                if (matches(declaration, "extend") || matches(declaration, "apply")) {
                    newList.addInheritedProperties(state, declaration.getExpression());
                }
                else {
                    newList.add(declaration);
                }
            }

            this.list = newList.list;
            for (int i = 0; i < list.size(); i++) {
                list.get(i).substituteValue(state, this, false, doCalculations);
            }
        }
        finally {
            state.popScope();
        }

        for (Declaration d : list) {
            result.add(new CssDeclaration(d.getName(), d.getExpression().evaluate(state, this), d.isImportant()));
        }
        return result;
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

    public List<Declaration> getAllDeclarations(String name) {
        ArrayList<Declaration> all = new ArrayList<Declaration>();

        for (Declaration declaration : list) {
            if (matches(declaration, name)) {
                all.add(declaration);
            }
        }

        return all;
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

    public Expression put(String name, Expression expression) {
        Expression result = get(name);
        list.add(new Declaration(name, expression));
        return result;
    }

    public Expression remove(String key) {
        Expression result = null;

        ListIterator<Declaration> it = list.listIterator();
        while (it.hasNext()) {
            Declaration current = it.next();
            if (matches(current, key)) {
                result = current.getExpression();
                it.remove();
            }
        }

        return result;
    }

    public void putAll(Map<? extends String, ? extends Expression> map) {
        for (Entry<? extends String, ? extends Expression> entry : map.entrySet()) {
            this.put(entry.getKey(), entry.getValue());
        }
    }

    public Map<String, Expression> asMap() {
        return this.mapView;
    }

    private class DeclarationListMapView implements Map<String, Expression> {
        public int size() {
            return DeclarationList.this.size();
        }

        public boolean isEmpty() {
            return DeclarationList.this.isEmpty();
        }

        public boolean containsKey(Object arg0) {
            return arg0 instanceof String && DeclarationList.this.containsKey((String) arg0);
        }

        public boolean containsValue(Object arg0) {
            return arg0 instanceof Expression && DeclarationList.this.containsValue((Expression) arg0);
        }

        public Expression get(Object arg0) {
            return arg0 instanceof String ? DeclarationList.this.get((String) arg0) : null;
        }

        public Expression put(String arg0, Expression arg1) {
            return DeclarationList.this.put(arg0, arg1);
        }

        public Expression remove(Object arg0) {
            if (arg0 instanceof String) {
                return DeclarationList.this.remove((String) arg0);
            }
            return null;
        }

        public void putAll(Map<? extends String, ? extends Expression> arg0) {
            DeclarationList.this.putAll(arg0);
        }

        public void clear() {
            DeclarationList.this.clear();
        }

        public Set<String> keySet() {
            return new DeclarationListKeySet();
        }

        public Collection<Expression> values() {
            return new DeclarationListValues();
        }

        public Set<Entry<String, Expression>> entrySet() {
            return new DeclarationListEntrySet();
        }

        private class DeclarationListKeySet implements Set<String> {
            final Set<String> keys = new LinkedHashSet<String>();

            public DeclarationListKeySet() {
                for (Declaration declaration : DeclarationList.this.list) {
                    keys.add(declaration.getName());
                }
            }

            public int size() {
                return keys.size();
            }

            public boolean isEmpty() {
                return keys.isEmpty();
            }

            public boolean contains(Object arg0) {
                return keys.contains(arg0);
            }

            public Iterator<String> iterator() {
                return new Iterator<String>() {
                    final Iterator<String> it = keys.iterator();
                    private String current = null;

                    public boolean hasNext() {
                        return it.hasNext();
                    }

                    public String next() {
                        return current = it.next();
                    }

                    public void remove() {
                        keys.remove(current);
                        DeclarationList.this.remove(current);
                    }
                };
            }

            public Object[] toArray() {
                return keys.toArray();
            }

            public <T> T[] toArray(T[] arg0) {
                return keys.toArray(arg0);
            }

            public boolean add(String arg0) {
                throw new UnsupportedOperationException("Cannot call add to a key set.");
            }

            public boolean remove(Object arg0) {
                if (!(arg0 instanceof String)) {
                    return false;
                }
                String key = (String) arg0;
                DeclarationList.this.remove(key);
                return keys.remove(key);
            }

            public boolean containsAll(Collection<?> arg0) {
                return keys.containsAll(arg0);
            }

            public boolean addAll(Collection<? extends String> arg0) {
                throw new UnsupportedOperationException("Cannot call addAll on a key set.");
            }

            public boolean retainAll(Collection<?> arg0) {
                keys.retainAll(arg0);
                    throw new UnsupportedOperationException("Not supported yet.");
            }

            public boolean removeAll(Collection<?> arg0) {
                boolean result = false;
                for (Object o : arg0) {
                    result |= remove(o);
                }
                return result;
            }

            public void clear() {
                keys.clear();
                DeclarationList.this.clear();
            }
        }

        private class DeclarationListEntrySet implements Set<Entry<String, Expression>> {
            public int size() {
                return DeclarationList.this.size();
            }

            public boolean isEmpty() {
                return DeclarationList.this.isEmpty();
            }

            public boolean contains(Object arg0) {
                return DeclarationList.this.contains(arg0);
            }

            public Iterator<Entry<String, Expression>> iterator() {
                return new Iterator<Entry<String, Expression>>() {
                    final Iterator<Declaration> it = DeclarationList.this.iterator();
                    public boolean hasNext() {
                        return it.hasNext();
                    }

                    public Entry<String, Expression> next() {
                        return it.next();
                    }

                    public void remove() {
                        it.remove();
                    }
                };
            }

            public Object[] toArray() {
                return DeclarationList.this.toArray();
            }

            public <T> T[] toArray(T[] arg0) {
                return DeclarationList.this.toArray(arg0);
            }

            public boolean add(Entry<String, Expression> arg0) {
                throw new UnsupportedOperationException("Cannot call add on an entry set.");
            }

            public boolean remove(Object arg0) {
                return DeclarationList.this.remove(arg0);
            }

            public boolean containsAll(Collection<?> arg0) {
                return DeclarationList.this.containsAll(arg0);
            }

            public boolean addAll(Collection<? extends Entry<String, Expression>> arg0) {
                throw new UnsupportedOperationException("Cannot call addAll on an entry set.");
            }

            public boolean retainAll(Collection<?> arg0) {
                return DeclarationList.this.retainAll(arg0);
            }

            public boolean removeAll(Collection<?> arg0) {
                return DeclarationList.this.removeAll(arg0);
            }

            public void clear() {
                DeclarationList.this.clear();
            }
        }

        private class DeclarationListValues implements Collection<Expression> {
            public int size() {
                return DeclarationList.this.size();
            }

            public boolean isEmpty() {
                return DeclarationList.this.isEmpty();
            }

            public boolean contains(Object arg0) {
                return arg0 instanceof Expression && DeclarationList.this.containsValue((Expression) arg0);
            }

            public Iterator<Expression> iterator() {
                return new Iterator<Expression>() {
                    Iterator<Declaration> it = DeclarationList.this.iterator();
                    public boolean hasNext() {
                        return it.hasNext();
                    }

                    public Expression next() {
                        return it.next().getExpression();
                    }

                    public void remove() {
                        it.remove();
                    }
                };
            }

            public Object[] toArray() {
                return toArray(new Object[size()]);
            }

            @SuppressWarnings("unchecked")
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

            public boolean add(Expression arg0) {
                throw new UnsupportedOperationException("Cannot call add on a value set.");
            }

            public boolean remove(Object arg0) {
                boolean result = false;

                Iterator<Declaration> it = DeclarationList.this.iterator();
                while (it.hasNext()) {
                    Declaration declaration = it.next();
                    if (declaration.getExpression().equals(arg0)) {
                        result = true;
                        it.remove();
                    }
                }

                return result;
            }

            public boolean containsAll(Collection<?> arg0) {
                for (Object o : arg0) {
                    if (!contains(o)) {
                        return false;
                    }
                }
                return true;
            }

            public boolean addAll(Collection<? extends Expression> arg0) {
                throw new UnsupportedOperationException("Cannot call addAll on a value set.");
            }

            public boolean removeAll(Collection<?> arg0) {
                boolean result = false;
                for (Object o : arg0) {
                    result |= remove(o);
                }
                return result;
            }

            public boolean retainAll(Collection<?> arg0) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            public void clear() {
                DeclarationList.this.clear();
            }
        }
    }
}
