package main.Tokenizer;

public class Token {
    final TokenTypeEnum tokenType;
    final String tokenValue;
    final int line;

    public Token(String tokenValue, TokenTypeEnum tokenType, int line){
        this.tokenType = tokenType;
        this.line = line;
        this.tokenValue = getMarkUpToken(tokenValue);
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
        return tokenType.getTaggedToken(tokenValue);
    }
}
