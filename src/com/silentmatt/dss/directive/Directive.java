/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.silentmatt.dss.directive;

import com.silentmatt.dss.*;

/**
 *
 * @author Matthew Crumley
 */
public abstract class Directive extends Rule {
    public abstract String getName();
    public abstract DirectiveType getType();
}
