package main.CompileEngine;

public interface Visitor {
    void compileClass();
    void compileClassVarDec();
    void compileSubroutineDec();
    void compileParameterList();
    void compileSubroutineBody();
    void compileVarDec();
    void compileStatements();
    void compileLet();
    void compileWhile();
    void compileDo();
    void compileReturn();
    void compileExpr();
    void compileTerm();
    void compileExprList();
}
