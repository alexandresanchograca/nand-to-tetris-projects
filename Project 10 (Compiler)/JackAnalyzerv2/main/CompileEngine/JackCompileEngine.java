package main.CompileEngine;

import main.Tokenizer.*;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class JackCompileEngine {

    private JackTokenizer jackTokenizer;
    private TokenFileHandler fileHandler;
    private List<Token> tokens;
    private List<Token> tokensTree;
    int currentToken = 0;

    public JackCompileEngine(String filename) throws IOException {
        this.tokensTree = new LinkedList<>();


        this.jackTokenizer = new JackTokenizer(filename);
        jackTokenizer.run();
        this.tokens = jackTokenizer.getTokens();
        this.fileHandler = jackTokenizer.getFileHandler();

        //Do our compilation
        compileClass(); //Every compiled file is a single class so we can start here

        fileHandler.writeFile(tokensTree);
    }



    void compileClass(){
        Token currentTokenVal = advance();

        if( !currentTokenVal.getTokenType().equals(TokenTypeEnum.KEYWORD) || !currentTokenVal.getTokenValue().equals("class") ){
            System.err.println("No class definition was provided");
            return;
        }
        Token classToken = new Token("", TokenTypeEnum.CLASS_DEC, currentToken);
        classToken.addChild( currentTokenVal );

        currentTokenVal = advance();
        if( !currentTokenVal.getTokenType().equals(TokenTypeEnum.IDENTIFIER) ){
            System.err.println("No class identifier was provided");
            return;
        }
        classToken.addChild( currentTokenVal );

        currentTokenVal = advance();
        if( !currentTokenVal.getTokenType().equals(TokenTypeEnum.SYMBOL) || !currentTokenVal.getTokenValue().equals("{") ){
            System.err.println("No class symbol was provided");
            return;
        }
        classToken.addChild( currentTokenVal );

        //Populating our class
        currentTokenVal = advance();
        compileClassVarDec(classToken);
        compileSubroutineDec(classToken);

        //System.out.println(classToken);

        currentTokenVal = advance();
        if( !currentTokenVal.getTokenType().equals(TokenTypeEnum.SYMBOL) || !currentTokenVal.getTokenValue().equals("}") ){
            System.err.println("No closing class symbol was provided");
            return;
        }
        classToken.addChild( currentTokenVal );

        tokensTree.add(classToken);
        //System.out.println(classToken);
    }

    void compileClassVarDec(Token parentToken){
        Token currentTokenVal = previous();

        //<classVarDec> <keyword> static
        if( !currentTokenVal.getTokenValue().equals("static") && !currentTokenVal.getTokenValue().equals("field") ){
            return ;
        }
        Token nonTerminalToken = new Token("", TokenTypeEnum.CLASS_VAR_DEC, currentToken);
        nonTerminalToken.addChild( currentTokenVal );

        //<keyword> int, boolean or char
        currentTokenVal = advance();
        if( !TokenMap.getInstance().containsPrimitiveTypeKey(currentTokenVal.getTokenValue()) && !currentTokenVal.getTokenType().equals(TokenTypeEnum.IDENTIFIER) ){
            System.err.println("No valid type was provided");
            return;
        }
        nonTerminalToken.addChild( currentTokenVal );

        //<identifier> anyValidIdentifier
        currentTokenVal = advance();
        if( !currentTokenVal.getTokenType().equals(TokenTypeEnum.IDENTIFIER)){
            System.err.println("No valid identifier was provided");
            return;
        }
        nonTerminalToken.addChild( currentTokenVal );

        compileNextVarDec(nonTerminalToken);

        //<symbol> ;
        currentTokenVal = previous();
        if( !currentTokenVal.getTokenType().equals(TokenTypeEnum.SYMBOL) || !currentTokenVal.getTokenValue().equals(";") ){
            System.err.println("No closing symbol was provided");
            return;
        }
        nonTerminalToken.addChild( currentTokenVal );

        //Adding our sucessful token to the parent non-terminal element
        parentToken.addChild( nonTerminalToken );

        //Recursive call
        advance();
        compileClassVarDec(parentToken);
    }

    void compileSubroutineDec(Token parentToken){
        Token currentTokenVal = previous();

        //<subroutineDec> <keyword> constructor | method | function
        if( !currentTokenVal.getTokenType().equals(TokenTypeEnum.KEYWORD) || !TokenMap.getInstance().containsSubRoutineDec(currentTokenVal.getTokenValue()) ){
            return ;
        }
        Token nonTerminalToken = new Token("", TokenTypeEnum.SUB_ROUTINE_DEC, currentToken);
        nonTerminalToken.addChild( currentTokenVal );

        //<keyword> void | int | char | boolean </keyword>
        currentTokenVal = advance();

        if(!TokenMap.getInstance().containsSubRoutineType(currentTokenVal.getTokenValue()) && !currentTokenVal.getTokenType().equals(TokenTypeEnum.IDENTIFIER)){
            System.err.println("No valid subroutine type was provided");
            return;
        }
        nonTerminalToken.addChild( currentTokenVal );

        //<identifier> _anything
        currentTokenVal = advance();
        if( !currentTokenVal.getTokenType().equals(TokenTypeEnum.IDENTIFIER)){
            System.err.println("No subroutine identifier was provided");
            return;
        }
        nonTerminalToken.addChild( currentTokenVal );

        //<symbol> (
        currentTokenVal = advance();
        if( !currentTokenVal.getTokenType().equals(TokenTypeEnum.SYMBOL) || !currentTokenVal.getTokenValue().equals("(")  ){
            System.err.println("No valid subroutine declaration was provided");
            return;
        }
        nonTerminalToken.addChild( currentTokenVal );

        //Set our parameter list
        Token paramList = new Token("", TokenTypeEnum.PARAMETER_LIST, currentToken);
        compileParameterList( paramList );
        nonTerminalToken.addChild( paramList );

        currentTokenVal = previous();
        if( !currentTokenVal.getTokenType().equals(TokenTypeEnum.SYMBOL) || !currentTokenVal.getTokenValue().equals(")")  ){
            System.err.println("No valid subroutine declaration was provided");
            return;
        }
        nonTerminalToken.addChild( currentTokenVal );

        //Set our subroutineBody
        Token subroutineBody = new Token("", TokenTypeEnum.SUB_ROUTINE_BODY, currentToken);
        currentTokenVal = advance();
        compileSubroutineBody( subroutineBody );

        nonTerminalToken.addChild( subroutineBody );

        //Adding our sucessful token to the parent non-terminal element
        parentToken.addChild( nonTerminalToken );

        //Recursive call
        advance();
        compileSubroutineDec(parentToken);
    }

    void compileParameterList(Token parentToken){
        Token currentTokenVal = previous();

        //<keyword> int | ...
        currentTokenVal = advance();
        if( !currentTokenVal.getTokenType().equals(TokenTypeEnum.KEYWORD) || !TokenMap.getInstance().containsPrimitiveTypeKey(currentTokenVal.getTokenValue())  ){
            return;
        }
        parentToken.addChild( currentTokenVal );

        //<identifier> paramName
        currentTokenVal = advance();
        if( !currentTokenVal.getTokenType().equals(TokenTypeEnum.IDENTIFIER)){
            System.err.println("No identifier was provided");
            return;
        }
        parentToken.addChild( currentTokenVal );

        //<symbol> ,
        currentTokenVal = advance();
        if( currentTokenVal.getTokenValue().equals(",") ){
            parentToken.addChild( currentTokenVal );
            compileParameterList(parentToken);
            return;
        }

        //parentToken.addChild( currentTokenVal );
    }

    void compileSubroutineBody(Token parentToken){
        Token currentTokenVal = previous();

        //<symbol> {
        if( !currentTokenVal.getTokenType().equals(TokenTypeEnum.SYMBOL) || !currentTokenVal.getTokenValue().equals("{")  ){
            System.err.println("No subroutine body defined!");
            return;
        }
        parentToken.addChild( currentTokenVal );

        //Check for variable declaration at the beggining of our subroutine
        advance();
        compileVarDec(parentToken);

        //Check for statements
        Token statements = new Token("", TokenTypeEnum.STATEMENTS, currentToken);
        compileStatements(statements);

        //<symbol> {
        currentTokenVal = previous();
        if( !currentTokenVal.getTokenType().equals(TokenTypeEnum.SYMBOL) || !currentTokenVal.getTokenValue().equals("}")  ){
            System.err.println("No subroutine body defined!");
            return;
        }



        parentToken.addChild( statements );

        parentToken.addChild( currentTokenVal );
    }

    void compileVarDec(Token parentToken){
        Token currentTokenVal = previous();

        if( !currentTokenVal.getTokenType().equals(TokenTypeEnum.KEYWORD) || !currentTokenVal.getTokenValue().equals("var")){
            return ;
        }
        Token nonTerminalToken = new Token("", TokenTypeEnum.VAR_DEC, currentToken);
        nonTerminalToken.addChild( currentTokenVal );

        currentTokenVal = advance();
        if(!TokenMap.getInstance().containsPrimitiveTypeKey(currentTokenVal.getTokenValue()) && !currentTokenVal.getTokenType().equals(TokenTypeEnum.IDENTIFIER)){
            System.err.println("Not valid declaration");
            return ;
        }
        nonTerminalToken.addChild( currentTokenVal );

        currentTokenVal = advance();
        if( !currentTokenVal.getTokenType().equals(TokenTypeEnum.IDENTIFIER)){
            System.err.println("Not valid declaration");
            return ;
        }
        nonTerminalToken.addChild( currentTokenVal );

        //This can be a var list
        compileNextVarDec(nonTerminalToken);

        currentTokenVal = previous();
        if( !currentTokenVal.getTokenType().equals(TokenTypeEnum.SYMBOL) || !currentTokenVal.getTokenValue().equals(";")){
            System.err.println("Not valid declaration");
            return ;
        }
        nonTerminalToken.addChild( currentTokenVal );

        //Adding our successful token to the parent non-terminal element
        parentToken.addChild( nonTerminalToken );

        //Recursive call
        advance();
        compileVarDec(parentToken);
    }

    void compileNextVarDec(Token parentToken){
        Token currentTokenVal = advance();

        if( !currentTokenVal.getTokenType().equals(TokenTypeEnum.SYMBOL) || !currentTokenVal.getTokenValue().equals(",")){
            return ;
        }
        parentToken.addChild( currentTokenVal );

        currentTokenVal = advance();
        if( !currentTokenVal.getTokenType().equals(TokenTypeEnum.IDENTIFIER)){
            System.err.println("Not valid declaration");
            return ;
        }
        parentToken.addChild( currentTokenVal );

        compileNextVarDec(parentToken);
    }

    void compileStatements(Token parentToken){
        Token currentTokenVal = previous();

        //<keyword> do | let | if | ...
        if( !currentTokenVal.getTokenType().equals(TokenTypeEnum.KEYWORD)){
            return;
        }

        switch (currentTokenVal.getTokenValue()){
            case "let":
                compileLet(parentToken);
                break;
            case "do":
                compileDo(parentToken);
                break;
            case "while":
                compileWhile(parentToken);
                break;
            case "return":
                compileReturn(parentToken);
                break;
            case "if":
                compileIf(parentToken);
                break;
            default:
                return;
        }

        //Recursive call
        compileStatements(parentToken);
    }
    void compileLet(Token parentToken){
        Token currentTokenVal = previous();
        Token letStatement = new Token("", TokenTypeEnum.LET_STATEMENT, currentToken);
        letStatement.addChild( currentTokenVal );

        currentTokenVal = advance();
        if( !currentTokenVal.getTokenType().equals(TokenTypeEnum.IDENTIFIER)){
            System.err.println("Not a valid let statement");
            return;
        }
        letStatement.addChild( currentTokenVal );

        currentTokenVal = advance();
        if( !currentTokenVal.getTokenType().equals(TokenTypeEnum.SYMBOL) || !currentTokenVal.getTokenValue().equals("=")){
            System.err.println("Not a valid let statement");
            return;
        }
        letStatement.addChild( currentTokenVal );

        advance();
        compileExpr(letStatement);

        currentTokenVal = advance();
        if( !currentTokenVal.getTokenType().equals(TokenTypeEnum.SYMBOL) || !currentTokenVal.getTokenValue().equals(";")){
            System.err.println("Not a valid let statement");
            return;
        }
        letStatement.addChild( currentTokenVal );


        parentToken.addChild(letStatement);
        advance();
    }

    void compileWhile(Token parentToken){
        Token currentTokenVal = previous();
        Token whileStatement = new Token("", TokenTypeEnum.WHILE_STATEMENT, currentToken);
        whileStatement.addChild( currentTokenVal );

        currentTokenVal = advance();
        if( !currentTokenVal.getTokenType().equals(TokenTypeEnum.SYMBOL) || !currentTokenVal.getTokenValue().equals("(")){
            System.err.println("Not a valid while statement");
            return;
        }
        whileStatement.addChild( currentTokenVal );

        advance();
        compileExpr(whileStatement);

        currentTokenVal = advance();
        if( !currentTokenVal.getTokenType().equals(TokenTypeEnum.SYMBOL) || !currentTokenVal.getTokenValue().equals(")")){
            System.err.println("Not a valid while statement");
            return;
        }
        whileStatement.addChild( currentTokenVal );

        currentTokenVal = advance();
        if( !currentTokenVal.getTokenType().equals(TokenTypeEnum.SYMBOL) || !currentTokenVal.getTokenValue().equals("{")){
            System.err.println("Not a valid while statement");
            return;
        }
        whileStatement.addChild( currentTokenVal );

        advance();
        Token statements = new Token("", TokenTypeEnum.STATEMENTS, currentToken);
        compileStatements(statements);
        whileStatement.addChild(statements);

        currentTokenVal = previous();
        if( !currentTokenVal.getTokenType().equals(TokenTypeEnum.SYMBOL) || !currentTokenVal.getTokenValue().equals("}")){
            System.err.println("Not a valid while statement");
            return;
        }
        whileStatement.addChild( currentTokenVal );

        parentToken.addChild(whileStatement);
        advance();
    }

    void compileDo(Token parentToken){
        Token currentTokenVal = previous();
        Token doStatement = new Token("", TokenTypeEnum.DO_STATEMENT, currentToken);
        doStatement.addChild( currentTokenVal );

        currentTokenVal = advance();
        if( !currentTokenVal.getTokenType().equals(TokenTypeEnum.IDENTIFIER)){
            System.err.println("Not a valid do statement");
            return;
        }
        doStatement.addChild( currentTokenVal );

        //There can be function() or obj.method()
        currentTokenVal = advance();
        if(currentTokenVal.getTokenValue().equals(".")){
            doStatement.addChild( currentTokenVal );

            currentTokenVal = advance();
            if( !currentTokenVal.getTokenType().equals(TokenTypeEnum.IDENTIFIER)){
                System.err.println("Not a valid do statement");
                return;
            }
            doStatement.addChild( currentTokenVal );

            currentTokenVal = advance();
        }

        if( !currentTokenVal.getTokenType().equals(TokenTypeEnum.SYMBOL) || !currentTokenVal.getTokenValue().equals("(")){
            System.err.println("Not a valid do statement");
            return;
        }
        doStatement.addChild( currentTokenVal );

        currentTokenVal = advance();
        Token expressionList = new Token("", TokenTypeEnum.EXPRESSION_LIST, currentToken);
        compileExprList(expressionList);
        doStatement.addChild(expressionList);

        currentTokenVal = previous();
        if( !currentTokenVal.getTokenType().equals(TokenTypeEnum.SYMBOL) || !currentTokenVal.getTokenValue().equals(")")){
            System.err.println("Not a valid do statement");
            return;
        }
        doStatement.addChild( currentTokenVal );

        currentTokenVal = advance();
        if( !currentTokenVal.getTokenType().equals(TokenTypeEnum.SYMBOL) || !currentTokenVal.getTokenValue().equals(";")){
            System.err.println("Not a valid do statement");
            return;
        }
        doStatement.addChild( currentTokenVal );


        parentToken.addChild(doStatement);
        advance();
    }

    void compileReturn(Token parentToken){
        Token currentTokenVal = previous();
        Token retStatement = new Token("", TokenTypeEnum.RET_STATEMENT, currentToken);
        retStatement.addChild( currentTokenVal );

        //compileExpr(retStatement);
        currentTokenVal = advance();
        if( !currentTokenVal.getTokenValue().equals(";")){
            compileExpr(retStatement);
            //retStatement.addChild( currentTokenVal );
            currentTokenVal = advance();
        }

        if( !currentTokenVal.getTokenType().equals(TokenTypeEnum.SYMBOL) || !currentTokenVal.getTokenValue().equals(";")){
            System.err.println("Not a valid return statement");
            return;
        }
        retStatement.addChild( currentTokenVal );

        parentToken.addChild(retStatement);
        advance();
    }

    void compileIf(Token parentToken){
        Token currentTokenVal = previous();
        Token ifStatement = new Token("", TokenTypeEnum.IF_STATEMENT, currentToken);
        ifStatement.addChild( currentTokenVal );

        currentTokenVal = advance();
        if( !currentTokenVal.getTokenType().equals(TokenTypeEnum.SYMBOL) || !currentTokenVal.getTokenValue().equals("(")){
            System.err.println("Not a valid if statement");
            return;
        }
        ifStatement.addChild( currentTokenVal );

        advance();
        compileExpr(ifStatement);

        currentTokenVal = advance();
        if( !currentTokenVal.getTokenType().equals(TokenTypeEnum.SYMBOL) || !currentTokenVal.getTokenValue().equals(")")){
            System.err.println("Not a valid if statement");
            return;
        }
        ifStatement.addChild( currentTokenVal );

        currentTokenVal = advance();
        if( !currentTokenVal.getTokenType().equals(TokenTypeEnum.SYMBOL) || !currentTokenVal.getTokenValue().equals("{")){
            System.err.println("Not a valid if statement");
            return;
        }
        ifStatement.addChild( currentTokenVal );

        advance();
        Token ifInsideStatements = new Token("", TokenTypeEnum.STATEMENTS, currentToken);
        compileStatements(ifInsideStatements);
        ifStatement.addChild(ifInsideStatements);

        currentTokenVal = previous();
        if( !currentTokenVal.getTokenType().equals(TokenTypeEnum.SYMBOL) || !currentTokenVal.getTokenValue().equals("}")){
            System.err.println("Not a valid if statement");
            return;
        }
        ifStatement.addChild( currentTokenVal );

        currentTokenVal = advance();
        if( !currentTokenVal.getTokenType().equals(TokenTypeEnum.KEYWORD) || !currentTokenVal.getTokenValue().equals("else")){
            parentToken.addChild(ifStatement);
            return;
        }
        ifStatement.addChild( currentTokenVal );

        currentTokenVal = advance();
        if( !currentTokenVal.getTokenType().equals(TokenTypeEnum.SYMBOL) || !currentTokenVal.getTokenValue().equals("{")){
            System.err.println("Not a valid else statement");
            return;
        }
        ifStatement.addChild( currentTokenVal );

        advance();
        Token elseInsideStatements = new Token("", TokenTypeEnum.STATEMENTS, currentToken);
        compileStatements(elseInsideStatements);
        ifStatement.addChild(elseInsideStatements);

        currentTokenVal = previous();
        if( !currentTokenVal.getTokenType().equals(TokenTypeEnum.SYMBOL) || !currentTokenVal.getTokenValue().equals("}")){
            System.err.println("Not a valid else statement");
            return;
        }
        ifStatement.addChild( currentTokenVal );

        parentToken.addChild(ifStatement);

        advance();
    }

    void compileExpr(Token parentToken){
        Token currentTokenVal = previous();

        Token expr = new Token("", TokenTypeEnum.EXPRESSION, currentToken);

        compileTerm(expr);

        parentToken.addChild(expr);
    }

    void compileTerm(Token parentToken){
        Token currentTokenVal = previous();
        Token term = new Token("", TokenTypeEnum.TERM, currentToken);

        //currentTokenVal = advance();
        if( !currentTokenVal.getTokenType().equals(TokenTypeEnum.IDENTIFIER) && !currentTokenVal.getTokenType().equals(TokenTypeEnum.KEYWORD)){
            System.err.println("Not a valid term");
            return;
        }
        term.addChild( currentTokenVal );

        parentToken.addChild(term);
    }

    void compileExprList(Token parentToken){
        Token currentTokenVal = previous();
        if( currentTokenVal.getTokenType().equals(TokenTypeEnum.KEYWORD) || currentTokenVal.getTokenType().equals(TokenTypeEnum.IDENTIFIER)){
            compileExpr(parentToken);
            currentTokenVal = advance();
            //parentToken.addChild( currentTokenVal );

            if(currentTokenVal.getTokenValue().equals(",")){
                parentToken.addChild( currentTokenVal );
                advance();
                compileExprList(parentToken);
            }

        }
    }

     private Token advance(){
        if(hasMoreTokens()) {
            currentToken++;
        }

        return previous();
    }

    private boolean hasMoreTokens(){
        return tokens.size() > currentToken;
    }

    private Token peek(){
        return tokens.get(currentToken);
    }

    private Token previous(){
        return tokens.get(currentToken - 1);
    }

    private void addTreeToken(Token token){
        tokensTree.add(token);
    }
}
