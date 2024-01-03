package main.Tokenizer;

import main.CodeGeneration.SubroutineSig;

import java.util.HashMap;
import java.util.Map;

public class TokenMap {

    private static TokenMap instance;
    private final Map<String, TokenType> symbolsMap;
    private final Map<String, TokenType> keywordsMap;

    private final Map<String, TokenType> primitiveTypes;
    private final Map<String, SubroutineSig> subroutineDecs;
    private final Map<String, TokenType> subroutineTypes;
    private final Map<String, TokenType> statementTypes;

    private final Map<String, TokenType> termSymbols;
    private final Map<String, String> operatorMap;
    private TokenMap(){
        this.keywordsMap = setMapKeywords();
        this.symbolsMap = setSymbolsMap();

        //Aditional maps
        this.primitiveTypes = setPrimitiveTypes();
        this.subroutineDecs = setSubroutineDecs();
        this.subroutineTypes = setSubroutineTypes();
        this.statementTypes = setStatementTypes();
        this.termSymbols = setTermSymbols();
        this.operatorMap = setOperatorMap();
    };


    public static TokenMap getInstance() {
        if(instance == null){
            instance = new TokenMap();
        }

        return instance;
    }

    public boolean containsSymbolKey(String key){
        return symbolsMap.containsKey(key);
    }

    public boolean containsKeywordKey(String key){
        return keywordsMap.containsKey(key);
    }

    public boolean containsPrimitiveTypeKey(String key){
        return primitiveTypes.containsKey(key);
    }

    public boolean containsSubRoutineDec(String key){
        return subroutineDecs.containsKey(key);
    }

    public boolean containsSubRoutineType(String key){
        return subroutineTypes.containsKey(key);
    }

    public boolean containsTermSymbol(String key){
        return termSymbols.containsKey(key);
    }

    public boolean containsOperator(String key){
        return operatorMap.containsKey(key);
    }

    public boolean containsStatement(String key){
        return statementTypes.containsKey(key);
    }

    public TokenType getSymbol(String key){
        return symbolsMap.get(key);
    }

    public TokenType getKeyword(String key){
        return keywordsMap.get(key);
    }

    public String getOperatorCommand(String key){
        return operatorMap.get(key);
    }

    public SubroutineSig getSubroutineDecs(String key) {
        return subroutineDecs.get(key);
    }

    private Map<String, TokenType> setMapKeywords(){
        Map<String, TokenType> tokenMap = new HashMap<>();
        tokenMap.put( "class", TokenType.KEYWORD);
        tokenMap.put( "constructor", TokenType.KEYWORD );
        tokenMap.put( "function", TokenType.KEYWORD );
        tokenMap.put( "method", TokenType.KEYWORD );
        tokenMap.put( "field", TokenType.KEYWORD );
        tokenMap.put( "static", TokenType.KEYWORD );
        tokenMap.put( "var", TokenType.KEYWORD );
        tokenMap.put( "int", TokenType.KEYWORD );
        tokenMap.put( "char", TokenType.KEYWORD );
        tokenMap.put( "boolean", TokenType.KEYWORD );
        tokenMap.put( "void", TokenType.KEYWORD );
        tokenMap.put( "true", TokenType.KEYWORD );
        tokenMap.put( "false", TokenType.KEYWORD );
        tokenMap.put( "null", TokenType.KEYWORD );
        tokenMap.put( "this", TokenType.KEYWORD );
        tokenMap.put( "let", TokenType.KEYWORD );
        tokenMap.put( "do", TokenType.KEYWORD );
        tokenMap.put( "if", TokenType.KEYWORD );
        tokenMap.put( "else", TokenType.KEYWORD );
        tokenMap.put( "while", TokenType.KEYWORD );
        tokenMap.put( "return", TokenType.KEYWORD );
        return tokenMap;
    }

    private Map<String, TokenType> setSymbolsMap(){
        Map<String, TokenType> tokenMap = new HashMap<>();
        tokenMap.put( "{", TokenType.SYMBOL );
        tokenMap.put( "}", TokenType.SYMBOL );
        tokenMap.put( "(", TokenType.SYMBOL );
        tokenMap.put( ")", TokenType.SYMBOL );
        tokenMap.put( "[", TokenType.SYMBOL );
        tokenMap.put( "]", TokenType.SYMBOL );
        tokenMap.put( ".", TokenType.SYMBOL );
        tokenMap.put( ",", TokenType.SYMBOL );
        tokenMap.put( ";", TokenType.SYMBOL );
        tokenMap.put( "+", TokenType.SYMBOL );
        tokenMap.put( "-", TokenType.SYMBOL );
        tokenMap.put( "*", TokenType.SYMBOL );
        tokenMap.put( "/", TokenType.SYMBOL );
        tokenMap.put( "&", TokenType.SYMBOL );
        tokenMap.put( "|", TokenType.SYMBOL );
        tokenMap.put( "<", TokenType.SYMBOL );
        tokenMap.put( ">", TokenType.SYMBOL );
        tokenMap.put( "=", TokenType.SYMBOL );
        tokenMap.put( "~", TokenType.SYMBOL );
        return tokenMap;
    }

    private Map<String, TokenType> setPrimitiveTypes(){
        Map<String, TokenType> tokenMap = new HashMap<>();
        tokenMap.put( "int", TokenType.KEYWORD );
        tokenMap.put( "char", TokenType.KEYWORD );
        tokenMap.put( "boolean", TokenType.KEYWORD );
        return tokenMap;
    }

    private Map<String, SubroutineSig> setSubroutineDecs(){
        Map<String, SubroutineSig> tokenMap = new HashMap<>();
        tokenMap.put( "constructor", SubroutineSig.CONSTRUCTOR);
        tokenMap.put( "method", SubroutineSig.METHOD );
        tokenMap.put( "function", SubroutineSig.FUNCTION );
        return tokenMap;
    }

    private Map<String, TokenType> setSubroutineTypes(){
        Map<String, TokenType> tokenMap = new HashMap<>();
        tokenMap.put( "void", TokenType.KEYWORD );
        tokenMap.put( "int", TokenType.KEYWORD );
        tokenMap.put( "char", TokenType.KEYWORD );
        tokenMap.put( "boolean", TokenType.KEYWORD );
        return tokenMap;
    }

    private Map<String, TokenType> setStatementTypes(){
        Map<String, TokenType> tokenMap = new HashMap<>();
        tokenMap.put( "let", TokenType.KEYWORD );
        tokenMap.put( "do", TokenType.KEYWORD );
        tokenMap.put( "while", TokenType.KEYWORD );
        tokenMap.put( "return", TokenType.KEYWORD );
        return tokenMap;
    }

    private Map<String, TokenType> setTermSymbols(){
        Map<String, TokenType> tokenMap = new HashMap<>();
        tokenMap.put( ".", TokenType.KEYWORD );
        tokenMap.put( "[", TokenType.KEYWORD );
        tokenMap.put( "(", TokenType.KEYWORD );
        return tokenMap;
    }

    private Map<String, String> setOperatorMap(){
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put( "+", "add" );
        tokenMap.put( "-", "sub" );
        tokenMap.put( "*", "call Math.multiply 2" );
        tokenMap.put( "/", "call Math.divide 2" );
        tokenMap.put( "&", "and" );
        tokenMap.put( "|", "or" );
        tokenMap.put( "<", "lt" );
        tokenMap.put( ">", "gt" );
        tokenMap.put( "=", "eq" );
        tokenMap.put( "~", "not" );
        return tokenMap;
    }
}
