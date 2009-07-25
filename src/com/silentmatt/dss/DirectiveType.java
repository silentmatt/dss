package com.silentmatt.dss;

/**
 * The type of a directive ({@literal @-rule}).
 *
 * @author Matthew Crumley
 */
public enum DirectiveType {
    /**
     * {@literal @media} directive.
     * @see MediaDirective
     */
    Media,

    /**
     * {@literal @import} directive.
     * @see ImportDirective
     */
    Import,

    /**
     * {@literal @include} directive.
     * @see IncludeDirective
     */
    Include,

    /**
     * {@literal @charset} directive.
     * @see CharsetDirective
     */
    Charset,

    /**
     * {@literal @page} directive.
     * @see PageDirective
     */
    Page,

    /**
     * {@literal @font-face} directive.
     * @see FontFaceDirective
     */
    FontFace,

    /**
     * {@literal @namespace} directive.
     * @see NamespaceDirective
     */
    Namespace,

    /**
     * {@literal @define} directive.
     * @see DefineDirective
     */
    Define,

    /**
     * {@literal @class} directive.
     * @see ClassDirective
     */
    Class,

    /**
     * Unknown directive type.
     * @see GenericDirective
     */
    Other
}
