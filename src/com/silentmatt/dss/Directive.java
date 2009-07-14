/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.silentmatt.dss;

/**
 *
 * @author matt
 */
public interface Directive extends Statement {
    String getName();
    DirectiveType getType();
}
