package main.Tokenizer;

import java.util.LinkedList;
import java.util.List;

public class Token {
    final TokenTypeEnum tokenType;
    final String tokenValue;
    final int line;
    private List<Token> childTokens;

    public Token(String tokenValue, TokenTypeEnum tokenType, int line){
        this.childTokens = new LinkedList<>();
        this.tokenType = tokenType;
        this.line = line;
        this.tokenValue = tokenValue;
        //this.tokenValue = getMarkUpToken(tokenValue);
    }

    private String getMarkUpToken(String tokenVal){
        String returnValue = "";
        switch (tokenVal){
            case "<":
                returnValue = "&lt;";
                break;
            case ">":
                returnValue = "&gt;";
                break;
            case "&":
                returnValue = "&amp;";
                break;
            default:
                returnValue = tokenVal;
                break;
        }
        return returnValue;
    }

    @Override
    public String toString() {

        String childValues = "";
        for(Token tempToken : childTokens){
            childValues += tempToken.toString();
        }

        return tokenType.getTaggedToken(getMarkUpToken(tokenValue) + childValues);
    }

    public TokenTypeEnum getTokenType() {
        return tokenType;
    }

    public String getTokenValue() {
        return tokenValue;
    }

    public void addChild(Token token){
        childTokens.add(token);
    }

    public Token getChild(int index){
        return childTokens.get(index);
    }

    public int getChildSize(){
        return childTokens.size();
    }
}
