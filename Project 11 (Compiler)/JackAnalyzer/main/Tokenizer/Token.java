package main.Tokenizer;

import main.CompileEngine.TokenCategory;

import java.util.LinkedList;
import java.util.List;

public class Token {
    final TokenType tokenType;
    String tokenValue;
    final int line;
    private List<Token> childTokens;
    private TokenCategory tokenCategory;
    private String identifierType;

    public Token(String tokenValue, TokenType tokenType, int line){
        this.childTokens = new LinkedList<>();
        this.tokenType = tokenType;
        this.line = line;
        this.tokenValue = tokenValue;
        this.tokenCategory = TokenCategory.NOT_DEFINED;
        this.identifierType = "";
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

    public TokenType getTokenType() {
        return tokenType;
    }

    public void setTokenValue(String tokenValue) {
        this.tokenValue = tokenValue;
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

    public int getLine() {
        return line;
    }

    public TokenCategory getTokenCategory() {
        return tokenCategory;
    }

    public void setTokenCategory(TokenCategory tokenCategory) {
        this.tokenCategory = tokenCategory;
    }

    public String getIdentifierType() {
        return identifierType;
    }

    public void setIdentifierType(String identifierType) {
        this.identifierType = identifierType;
    }

    public List<Token> getChilds() {
        return childTokens;
    }
}
