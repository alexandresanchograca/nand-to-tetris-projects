package main.Tokenizer;

import java.util.HashMap;
import java.util.Map;

public enum TokenTypeEnum {
    NOT_DEFINED(""),
    KEYWORD("keyword"),
    SYMBOL("symbol"),
    IDENTIFIER("identifier"),
    INT_CONST("integerConstant"),
    STRING_CONST("stringConstant"),

    //Used for the syntax tree
    CLASS_DEC("class"),
    CLASS_VAR_DEC("classVarDec"),
    SUB_ROUTINE_DEC("subroutineDec"),
    PARAMETER_LIST("parameterList"),
    SUB_ROUTINE_BODY("subroutineBody"),
    VAR_DEC("varDec"),
    STATEMENTS("statements"),
    LET_STATEMENT("letStatement"),
    DO_STATEMENT("doStatement"),
    RET_STATEMENT("returnStatement"),
    IF_STATEMENT("ifStatement"),
    WHILE_STATEMENT("whileStatement"),
    EXPRESSION("expression"),
    EXPRESSION_LIST("expressionList"),
    TERM("term");

    private final String tagValue;

    private TokenTypeEnum(String tagValue){
        this.tagValue = tagValue;
    }

    public String getTaggedToken(String token) {
        return this.equals(NOT_DEFINED) ? "" : "<" + tagValue + "> " + token + " </" + tagValue + ">";
    }
}
