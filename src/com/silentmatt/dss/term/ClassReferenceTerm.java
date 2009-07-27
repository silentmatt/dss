package com.silentmatt.dss.term;

import com.silentmatt.dss.ClassReference;

/**
 *
 * @author matt
 */
public class ClassReferenceTerm extends Term {
    private ClassReference classReference;

    public ClassReferenceTerm(ClassReference cls) {
        super();
        this.classReference = cls;
    }

    public ClassReference getClassReference() {
        return classReference;
    }

    public void setClassReference(ClassReference classReference) {
        this.classReference = classReference;
    }

    @Override
    public String toString() {
        return classReference.toString();
    }
}
