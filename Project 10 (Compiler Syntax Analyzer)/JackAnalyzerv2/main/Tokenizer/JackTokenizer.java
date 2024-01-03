package main.Tokenizer;


import java.io.*;
import java.util.*;

public class JackTokenizer {

    private final String codeString;
    private final TokenFileHandler fileHandler;
    private final List<Token> tokens;
    private int start = 0;
    private int current = 0;
    private int line = 1;
    private boolean hadError = false;
    private String sourceFilename;

    public JackTokenizer(String sourceFilename) throws IOException {
        this.sourceFilename = sourceFilename;
        this.fileHandler = new TokenFileHandler(sourceFilename);
        this.tokens = new LinkedList<>();
        this.codeString = fileHandler.openFile();
    }

    public void run() throws IOException {
        scanTokens();

        if(hadError){
            System.out.println("Compilation failed, an error occurred!");
            System.exit(1);
        }
    }

    public void writeOutputFile() throws IOException {
        fileHandler.writeFile(tokens);
    }

    public List<Token> scanTokens(){
        //Our loop to scan all the tokens in the string
        while(hasMoreTokens()){
            start = current;
            scanToken();
        }
        return tokens;
    }

    private void scanToken(){
        String tokenValue = advance().toString();

        if(tokenValue.equals("\n") || tokenValue.isEmpty() || tokenValue.equals(" ")){
            return;
        }

        //Special handling for comments
        if(isComment(tokenValue)){
            return;
        }

        TokenTypeEnum token = TokenTypeEnum.NOT_DEFINED;

        //Scanning single chars
        if( TokenMap.getInstance().containsSymbolKey( tokenValue ) ) {
            token = TokenMap.getInstance().getSymbol(tokenValue);
        }

        //scanning a string constant
        if(token.equals(TokenTypeEnum.NOT_DEFINED) && tokenValue.charAt(0) == '"'){
            getStringConst();
            tokenValue = codeString.substring(start + 1, current - 1);
            token = TokenTypeEnum.STRING_CONST;
        }
        //scanning a integer constant
        else if(isDigit(tokenValue.charAt(0))){
            getIntConst();
            tokenValue = codeString.substring(start, current);
            token = TokenTypeEnum.INT_CONST;

            if(!isValidRangeInt(tokenValue)){
                hadError = true;
                error(line, "Integer must be a value from 0 to 32767");
            }

        }
        //scanning a keyword or identifier
        else if(isAlpha(tokenValue)){
            getIdentifier();
            tokenValue = codeString.substring(start, current);

            if(TokenMap.getInstance().containsKeywordKey(tokenValue)){
                token = TokenTypeEnum.KEYWORD;
            }
            else{
                token = TokenTypeEnum.IDENTIFIER;
            }

        }

        if(token.equals(TokenTypeEnum.NOT_DEFINED)) {
            hadError = true;
            error(line, "An error ocurred, please check your code!");
            return;
        }

        tokens.add( new Token(tokenValue, token, line) );
    }

    private void getStringConst(){
        //Finding the end of our string by getting the other " char by char
        //At the end we have our string because we have the offset of start and current
        while ( peek() != '"' && hasMoreTokens() ){
            if(peek() == '\n') { line++; }
            advance();
        }
        advance(); //So we don't analyse the ending " again
    }

    private void getIntConst(){
        while ( isDigit(peek()) && hasMoreTokens() ){
            if(peek() == '\n') { line++; }
            advance();
        }
    }

    private void getIdentifier(){
        while ( isAlphaNumeric( peek() ) && hasMoreTokens() ){

            if(peek() == '\n') { line++; }

            advance();
        }
    }

    public boolean hasMoreTokens(){
        return current < codeString.length();
    }

    public Character advance(){

        if(!hasMoreTokens()){
            return '\0';
        }

        Character character = codeString.charAt(current++);

        if(character == '\n'){
            line++;
        }

        return character;
    }

    private Character peek(){
        if(!hasMoreTokens()){
            return '\0';
        }

        return codeString.charAt(current);
    }

    private boolean isDigit(char inChar){
        return inChar >= '0' && inChar <= '9';
    }

    private boolean isAlpha(String charValue){
        return charValue.matches("^[a-zA-Z_]$");
    }

    private boolean isAlphaNumeric(char charValue){
        return isAlpha(Character.toString(charValue)) || isDigit(charValue);
    }

    private boolean isValidRangeInt(String token){
        int tokenValue = Integer.parseInt(token);
        return tokenValue >= 0 && tokenValue <= 32767;
    }

    /* Really ugly analyser for comments */
    private boolean isComment(String tokenValue){
        if(tokenValue.equals("/")){
            if(match('/')){
                while(peek() != '\n'){
                    advance();
                }
                return true;
            }else if(match('*')){
                while(hasMoreTokens()){
                    if(peek() != '*'){
                        advance();
                    }
                    else {
                        advance();
                        if (peek() == '/') {
                            advance();
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean match(char expected){
        if(!hasMoreTokens()){
            return false;
        }

        //We peek into next char and see if it equals the expected one
        if(peek() != expected){
            return false;
        }

        current++;
        return true;
    }

    private void error(int line, String message) {
        report(line, sourceFilename, message);
    }
    private void report(int line, String where, String message) {
        System.err.println("[line " + line + "] Error" + where + ": " + message);
    }

    public TokenFileHandler getFileHandler() {
        return fileHandler;
    }

    public List<Token> getTokens() {
        return tokens;
    }
}
