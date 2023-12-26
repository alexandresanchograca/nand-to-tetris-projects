package main.Tokenizer;

import java.util.HashMap;
import java.util.Map;

public enum TokenTypeEnum {
    NOT_DEFINED(""),
    KEYWORD("keyword"),
    SYMBOL("symbol"),
    IDENTIFIER("identifier"),
    INT_CONST("integerConstant"),
    STRING_CONST("stringConstant");

    private final String tagValue;

    private TokenTypeEnum(String tagValue){
        this.tagValue = tagValue;
    }

    public String getTaggedToken(String token) {
        return this.equals(NOT_DEFINED) ? "" : "<" + tagValue + "> " + token + " </" + tagValue + ">";
    }
}
