package main.CodeGeneration;

import main.CompileEngine.TokenCategory;
import main.Tokenizer.Token;

public class TableEntry {
    private Token token;
    private long number;

    public TableEntry(Token token, long number){
        this.token = token;
        this.number = number;
    }

    public void setNumber(long number) {
        this.number = number;
    }

    public long getNumber() {
        return number;
    }

    public String getType() {
        return token.getIdentifierType();
    }

    public TokenCategory getCategory() {
        return token.getTokenCategory();
    }

    @Override
    public String toString() {
        return " TableEntry{" + "\n" +
                "name = " + token.getTokenValue() + ",\n" +
                "type = " + getType() + ",\n" +
                "kind = " + getCategory() + ",\n" +
                "number = " + number + ",\n" +
                "}\n";
    }
}
