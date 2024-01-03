package main.CompileEngine;

/** Used to classify identifiers so we later can create symbol tables */
public enum TokenCategory {
    NOT_DEFINED,
    CLASS,
    STATIC,
    FIELD,
    SUB_ROUTINE,
    ARGUMENT,
    VAR;

    public String getCategory(){
        return this.equals(NOT_DEFINED) ? "" : this.toString();
    }
}
