package main.Tokenizer;

import java.util.HashMap;
import java.util.Map;

public class TokenMap {

    private static TokenMap instance;
    private final Map<String, TokenTypeEnum> symbolsMap;
    private final Map<String, TokenTypeEnum> keywordsMap;

    private TokenMap(){
        this.keywordsMap = setMapKeywords();
        this.symbolsMap = setSymbolsMap();
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

    public TokenTypeEnum getSymbol(String key){
        return symbolsMap.get(key);
    }

    public TokenTypeEnum getKeyword(String key){
        return keywordsMap.get(key);
    }

    private Map<String, TokenTypeEnum> setMapKeywords(){
        Map<String, TokenTypeEnum> tokenMap = new HashMap<>();
        tokenMap.put( "class", TokenTypeEnum.KEYWORD);
        tokenMap.put( "constructor", TokenTypeEnum.KEYWORD );
        tokenMap.put( "function", TokenTypeEnum.KEYWORD );
        tokenMap.put( "method", TokenTypeEnum.KEYWORD );
        tokenMap.put( "field", TokenTypeEnum.KEYWORD );
        tokenMap.put( "static", TokenTypeEnum.KEYWORD );
        tokenMap.put( "var", TokenTypeEnum.KEYWORD );
        tokenMap.put( "int", TokenTypeEnum.KEYWORD );
        tokenMap.put( "char", TokenTypeEnum.KEYWORD );
        tokenMap.put( "boolean", TokenTypeEnum.KEYWORD );
        tokenMap.put( "void", TokenTypeEnum.KEYWORD );
        tokenMap.put( "true", TokenTypeEnum.KEYWORD );
        tokenMap.put( "false", TokenTypeEnum.KEYWORD );
        tokenMap.put( "null", TokenTypeEnum.KEYWORD );
        tokenMap.put( "this", TokenTypeEnum.KEYWORD );
        tokenMap.put( "let", TokenTypeEnum.KEYWORD );
        tokenMap.put( "do", TokenTypeEnum.KEYWORD );
        tokenMap.put( "if", TokenTypeEnum.KEYWORD );
        tokenMap.put( "else", TokenTypeEnum.KEYWORD );
        tokenMap.put( "while", TokenTypeEnum.KEYWORD );
        tokenMap.put( "return", TokenTypeEnum.KEYWORD );
        return tokenMap;
    }

    private Map<String, TokenTypeEnum> setSymbolsMap(){
        Map<String, TokenTypeEnum> tokenMap = new HashMap<>();
        tokenMap.put( "{", TokenTypeEnum.SYMBOL );
        tokenMap.put( "}", TokenTypeEnum.SYMBOL );
        tokenMap.put( "(", TokenTypeEnum.SYMBOL );
        tokenMap.put( ")", TokenTypeEnum.SYMBOL );
        tokenMap.put( "[", TokenTypeEnum.SYMBOL );
        tokenMap.put( "]", TokenTypeEnum.SYMBOL );
        tokenMap.put( ".", TokenTypeEnum.SYMBOL );
        tokenMap.put( ",", TokenTypeEnum.SYMBOL );
        tokenMap.put( ";", TokenTypeEnum.SYMBOL );
        tokenMap.put( "+", TokenTypeEnum.SYMBOL );
        tokenMap.put( "-", TokenTypeEnum.SYMBOL );
        tokenMap.put( "*", TokenTypeEnum.SYMBOL );
        tokenMap.put( "/", TokenTypeEnum.SYMBOL );
        tokenMap.put( "&", TokenTypeEnum.SYMBOL );
        tokenMap.put( "|", TokenTypeEnum.SYMBOL );
        tokenMap.put( "<", TokenTypeEnum.SYMBOL );
        tokenMap.put( ">", TokenTypeEnum.SYMBOL );
        tokenMap.put( "=", TokenTypeEnum.SYMBOL );
        tokenMap.put( "~", TokenTypeEnum.SYMBOL );
        return tokenMap;
    }
}
