package main.CompileEngine;

import main.CodeGeneration.*;
import main.Tokenizer.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class JackCompileEngine {
    private String filename;
    private JackTokenizer jackTokenizer;
    private TokenFileHandler fileHandler;
    private List<Token> tokens;
    private List<Token> tokensTree;
    private int currentToken = 0;
    private boolean hadError;
    private final SymbolTable symbolTable;
    private VMWriter vmWriter;
    private Label labelHandler;


    public JackCompileEngine(String filename) throws IOException {
        this.tokensTree = new LinkedList<>();
        this.jackTokenizer = new JackTokenizer(filename);
        this.filename = filename;
        this.hadError = false;
        this.symbolTable = new SymbolTable();
        this.labelHandler = new Label();
    }

    public void run() throws IOException {
        this.jackTokenizer.run();
        this.tokens = jackTokenizer.getTokens();
        this.fileHandler = jackTokenizer.getFileHandler();

        this.vmWriter = new VMWriter(filename);

        //Do our compilation
        compileClass(); //Every compiled file is a single class so we can start here

        fileHandler.writeFile(tokensTree);
        vmWriter.close();
    }

    private void compileClass(){
        Token currentTokenVal = advance();

        if( !currentTokenVal.getTokenType().equals(TokenType.KEYWORD) || !currentTokenVal.getTokenValue().equals("class") ){
            error(currentTokenVal.getLine(), "No class definition was provided");
            return;
        }
        Token classToken = new Token("", TokenType.CLASS_DEC, currentToken);
        classToken.addChild( currentTokenVal );

        currentTokenVal = advance();
        if( !currentTokenVal.getTokenType().equals(TokenType.IDENTIFIER) ){
            error(currentTokenVal.getLine(), "No class identifier was provided");
            return;
        }
        currentTokenVal.setTokenCategory(TokenCategory.CLASS);
        symbolTable.define(currentTokenVal);
        classToken.addChild( currentTokenVal );

        currentTokenVal = advance();
        if( !currentTokenVal.getTokenType().equals(TokenType.SYMBOL) || !currentTokenVal.getTokenValue().equals("{") ){
            error(currentTokenVal.getLine(), "No class symbol was provided");
            return;
        }
        classToken.addChild( currentTokenVal );

        //Populating our class
        currentTokenVal = advance();
        compileClassVarDec(classToken);
        compileSubroutineDec(classToken);

        currentTokenVal = advance();
        if( !currentTokenVal.getTokenType().equals(TokenType.SYMBOL) || !currentTokenVal.getTokenValue().equals("}") ){
            error(currentTokenVal.getLine(), "No closing class symbol was provided");
            return;
        }
        classToken.addChild( currentTokenVal );

        tokensTree.add(classToken);
    }

    private void compileClassVarDec(Token parentToken){
        Token currentTokenVal = previous();

        //<classVarDec> <keyword> static
        if( !currentTokenVal.getTokenValue().equals("static") && !currentTokenVal.getTokenValue().equals("field") ){
            return ;
        }
        Token nonTerminalToken = new Token("", TokenType.CLASS_VAR_DEC, currentToken);
        nonTerminalToken.addChild( currentTokenVal );

        //Getting the declaration category
        TokenCategory tokenCategory = currentTokenVal.getTokenValue().equals("static") ? TokenCategory.STATIC : TokenCategory.FIELD;

        //<keyword> int, boolean or char
        currentTokenVal = advance();
        if( !TokenMap.getInstance().containsPrimitiveTypeKey(currentTokenVal.getTokenValue()) && !currentTokenVal.getTokenType().equals(TokenType.IDENTIFIER) ){
            error(currentTokenVal.getLine(), "No valid type was provided");
            return;
        }
        nonTerminalToken.addChild( currentTokenVal );

        //Getting identifier type
        String identifierType = currentTokenVal.getTokenValue();

        //<identifier> anyValidIdentifier
        currentTokenVal = advance();
        if( !currentTokenVal.getTokenType().equals(TokenType.IDENTIFIER)){
            error(currentTokenVal.getLine(), "No valid identifier was provided");
            return;
        }
        currentTokenVal.setTokenCategory(tokenCategory);
        currentTokenVal.setIdentifierType(identifierType);
        symbolTable.define(currentTokenVal);
        nonTerminalToken.addChild( currentTokenVal );

        compileNextVarDec(nonTerminalToken, currentTokenVal);

        //<symbol> ;
        currentTokenVal = previous();
        if( !currentTokenVal.getTokenType().equals(TokenType.SYMBOL) || !currentTokenVal.getTokenValue().equals(";") ){
            error(currentTokenVal.getLine(), "No closing symbol was provided");
            return;
        }
        nonTerminalToken.addChild( currentTokenVal );

        //Adding our sucessful token to the parent non-terminal element
        parentToken.addChild( nonTerminalToken );

        //Recursive call
        advance();
        compileClassVarDec(parentToken);
    }

    private void compileSubroutineDec(Token parentToken){
        Token currentTokenVal = previous();

        //<subroutineDec> <keyword> constructor | method | function
        if( !currentTokenVal.getTokenType().equals(TokenType.KEYWORD) || !TokenMap.getInstance().containsSubRoutineDec(currentTokenVal.getTokenValue()) ){
            return ;
        }
        Token nonTerminalToken = new Token("", TokenType.SUB_ROUTINE_DEC, currentToken);
        nonTerminalToken.addChild( currentTokenVal );

        SubroutineSig subroutineSig = TokenMap.getInstance().getSubroutineDecs(currentTokenVal.getTokenValue());

        //<keyword> void | int | char | boolean </keyword>
        currentTokenVal = advance();
        if(!TokenMap.getInstance().containsSubRoutineType(currentTokenVal.getTokenValue()) && !currentTokenVal.getTokenType().equals(TokenType.IDENTIFIER)){
            error(currentTokenVal.getLine(), "No valid subroutine type was provided");
            return;
        }
        nonTerminalToken.addChild( currentTokenVal );

        String identifierType = currentTokenVal.getTokenValue();

        //<identifier> _anything
        currentTokenVal = advance();
        if( !currentTokenVal.getTokenType().equals(TokenType.IDENTIFIER)){
            error(currentTokenVal.getLine(), "No subroutine identifier was provided");
            return;
        }
        currentTokenVal.setTokenCategory(TokenCategory.SUB_ROUTINE);
        currentTokenVal.setIdentifierType(identifierType);
        labelHandler.resetLabels();
        symbolTable.startSubroutine(currentTokenVal);
        nonTerminalToken.addChild( currentTokenVal );

        if(subroutineSig.equals(SubroutineSig.METHOD)){
            Token thisObjToken = new Token("this", TokenType.KEYWORD, 0);
            thisObjToken.setTokenCategory(TokenCategory.ARGUMENT);
            thisObjToken.setIdentifierType(currentTokenVal.getIdentifierType());
            symbolTable.define(thisObjToken);
        }

        //<symbol> (
        currentTokenVal = advance();
        if( !currentTokenVal.getTokenType().equals(TokenType.SYMBOL) || !currentTokenVal.getTokenValue().equals("(")  ){
            error(currentTokenVal.getLine(), "No valid subroutine declaration was provided");
            return;
        }
        nonTerminalToken.addChild( currentTokenVal );

        //Set our parameter list
        Token paramList = new Token("", TokenType.PARAMETER_LIST, currentToken);
        compileParameterList( paramList );
        nonTerminalToken.addChild( paramList );

        currentTokenVal = previous();
        if( !currentTokenVal.getTokenType().equals(TokenType.SYMBOL) || !currentTokenVal.getTokenValue().equals(")")  ){
            error(currentTokenVal.getLine(), "No valid subroutine declaration was provided");
            return;
        }
        nonTerminalToken.addChild( currentTokenVal );

        //Set our subroutineBody
        Token subroutineBody = new Token("", TokenType.SUB_ROUTINE_BODY, currentToken);
        subroutineBody.setIdentifierType(subroutineSig.toString());
        currentTokenVal = advance();
        compileSubroutineBody( subroutineBody );

        nonTerminalToken.addChild( subroutineBody );

        //Adding our sucessful token to the parent non-terminal element
        parentToken.addChild( nonTerminalToken );

        //Recursive call
        advance();
        compileSubroutineDec(parentToken);
    }

    private void compileParameterList(Token parentToken){
        Token currentTokenVal = previous();

        //<keyword> int | ...
        currentTokenVal = advance();
        if(!TokenMap.getInstance().containsPrimitiveTypeKey(currentTokenVal.getTokenValue()) && !currentTokenVal.getTokenType().equals(TokenType.IDENTIFIER)   ){
            return;
        }
        parentToken.addChild( currentTokenVal );

        String identifierType = currentTokenVal.getTokenValue();

        //<identifier> paramName
        currentTokenVal = advance();
        if( !currentTokenVal.getTokenType().equals(TokenType.IDENTIFIER)){
            error(currentTokenVal.getLine(), "No identifier was provided");
            return;
        }
        currentTokenVal.setTokenCategory(TokenCategory.ARGUMENT);
        currentTokenVal.setIdentifierType(identifierType);
        symbolTable.define(currentTokenVal);
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

    private void compileSubroutineBody(Token parentToken){
        Token currentTokenVal = previous();

        //<symbol> {
        if( !currentTokenVal.getTokenType().equals(TokenType.SYMBOL) || !currentTokenVal.getTokenValue().equals("{")  ){
            error(currentTokenVal.getLine(), "No subroutine body defined!");
            return;
        }
        parentToken.addChild( currentTokenVal );

        //Check for variable declaration at the beggining of our subroutine
        advance();
        compileVarDec(parentToken);

        //Writing the function declaration and number of local variables
        vmWriter.writeFunction(symbolTable.getSubroutinePath(), symbolTable.varCount(TokenCategory.VAR));

        if(parentToken.getIdentifierType().equals("constructor")){
            //Define the number of class fields/variables that we want to allocate
            vmWriter.writePush(MemorySegments.CONSTANT, symbolTable.varCount(TokenCategory.FIELD));
            //We allocate the number of fields pushed to the stack
            vmWriter.writeCall("Memory.alloc", 1);
            //The return of the alloc indicates where our object was stored in memory
            // So we pop this values to our pointer 0 (this reference)
            vmWriter.writePop(MemorySegments.POINTER, 0);
        }
        else if(parentToken.getIdentifierType().equals("method")){
            //To get the reference for the this object
            vmWriter.writePush(MemorySegments.ARGUMENT, 0);
            vmWriter.writePop(MemorySegments.POINTER, 0);
        }

        //Check for statements
        Token statements = new Token("", TokenType.STATEMENTS, currentToken);
        compileStatements(statements);

        //<symbol> {
        currentTokenVal = previous();
        if( !currentTokenVal.getTokenType().equals(TokenType.SYMBOL) || !currentTokenVal.getTokenValue().equals("}")  ){
            error(currentTokenVal.getLine(), "No subroutine body defined!");
            return;
        }

        parentToken.addChild( statements );

        parentToken.addChild( currentTokenVal );
    }

    private void compileVarDec(Token parentToken){
        Token currentTokenVal = previous();

        if( !currentTokenVal.getTokenType().equals(TokenType.KEYWORD) || !currentTokenVal.getTokenValue().equals("var")){
            return ;
        }
        Token nonTerminalToken = new Token("", TokenType.VAR_DEC, currentToken);
        nonTerminalToken.addChild( currentTokenVal );

        currentTokenVal = advance();
        if(!TokenMap.getInstance().containsPrimitiveTypeKey(currentTokenVal.getTokenValue()) && !currentTokenVal.getTokenType().equals(TokenType.IDENTIFIER)){
            error(currentTokenVal.getLine(), "Not valid declaration");
            return ;
        }
        nonTerminalToken.addChild( currentTokenVal );

        String identifierType = currentTokenVal.getTokenValue();

        currentTokenVal = advance();
        if( !currentTokenVal.getTokenType().equals(TokenType.IDENTIFIER)){
            error(currentTokenVal.getLine(), "Not valid declaration");
            return ;
        }
        currentTokenVal.setTokenCategory(TokenCategory.VAR);
        currentTokenVal.setIdentifierType(identifierType);
        symbolTable.define(currentTokenVal);
        nonTerminalToken.addChild( currentTokenVal );

        //This can be a var list
        compileNextVarDec(nonTerminalToken, currentTokenVal);

        currentTokenVal = previous();
        if( !currentTokenVal.getTokenType().equals(TokenType.SYMBOL) || !currentTokenVal.getTokenValue().equals(";")){
            error(currentTokenVal.getLine(), "Not valid declaration");
            return ;
        }
        nonTerminalToken.addChild( currentTokenVal );

        //Adding our successful token to the parent non-terminal element
        parentToken.addChild( nonTerminalToken );

        //Recursive call
        advance();
        compileVarDec(parentToken);
    }

    private void compileNextVarDec(Token parentToken, Token followedToken){
        Token currentTokenVal = advance();

        if( !currentTokenVal.getTokenType().equals(TokenType.SYMBOL) || !currentTokenVal.getTokenValue().equals(",")){
            return ;
        }
        parentToken.addChild( currentTokenVal );

        currentTokenVal = advance();
        if( !currentTokenVal.getTokenType().equals(TokenType.IDENTIFIER)){
            error(currentTokenVal.getLine(), "Not valid declaration");
            return ;
        }
        currentTokenVal.setTokenCategory(followedToken.getTokenCategory());
        currentTokenVal.setIdentifierType(followedToken.getIdentifierType());
        symbolTable.define(currentTokenVal);
        parentToken.addChild( currentTokenVal );

        compileNextVarDec(parentToken, currentTokenVal);
    }

    private void compileStatements(Token parentToken){
        Token currentTokenVal = previous();

        //<keyword> do | let | if | ...
        if( !currentTokenVal.getTokenType().equals(TokenType.KEYWORD)){
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
    private void compileLet(Token parentToken){
        Token currentTokenVal = previous();
        Token letStatement = new Token("", TokenType.LET_STATEMENT, currentToken);
        letStatement.addChild( currentTokenVal );

        currentTokenVal = advance();
        if( !currentTokenVal.getTokenType().equals(TokenType.IDENTIFIER)){
            error(currentTokenVal.getLine(), "Not a valid let statement");
            return;
        }
        letStatement.addChild( currentTokenVal );

        String letStmtIdentifier = currentTokenVal.getTokenValue();

        boolean isArrayDeclaration = false;
        currentTokenVal = advance();
        if(currentTokenVal.getTokenValue().equals("[")){
            isArrayDeclaration = true;
            letStatement.addChild(currentTokenVal);

            currentTokenVal = advance();
            compileExpr(letStatement);

            //Before we got the integer value inside [] and push it to the stack
            //Now we want to push the array adress on the stack and add them
            //(ArrayBaseAdress + index) = Memory Adress that we want to manipulate
            //each word is a 16-bit register type independent, so we dont calculate the size of each Type
            vmWriter.writePush(symbolTable.kindOfMem(letStmtIdentifier), symbolTable.indexOf(letStmtIdentifier));
            vmWriter.writeArithmetic("add");

            currentTokenVal = previous();
            letStatement.addChild( currentTokenVal );
            currentTokenVal = advance();
        }

        //currentTokenVal = advance();
        if( !currentTokenVal.getTokenType().equals(TokenType.SYMBOL) || !currentTokenVal.getTokenValue().equals("=")){
            error(currentTokenVal.getLine(), "Not a valid let statement");
            return;
        }
        letStatement.addChild( currentTokenVal );

        currentTokenVal = advance();
        compileExpr(letStatement);

        currentTokenVal = previous();
        if( !currentTokenVal.getTokenType().equals(TokenType.SYMBOL) || !currentTokenVal.getTokenValue().equals(";")){
            error(currentTokenVal.getLine(), "Not a valid let statement");
            return;
        }
        letStatement.addChild( currentTokenVal );

        if(isArrayDeclaration){
            //Handling array declarations
            // a[i] = b[j] -> this is just to do this assignment
            vmWriter.writePop(MemorySegments.TEMP, 0); //Storing the value of the second expression on temp 0 address
            vmWriter.writePop(MemorySegments.POINTER, 1); //Storing the value of the left array expr on pointer 1 (that)
            vmWriter.writePush(MemorySegments.TEMP, 0); //Pushing the left array expr to the stack
            vmWriter.writePop(MemorySegments.THAT, 0); //Assigning our left expr the value of the right expr
        }else {
            vmWriter.writePop(symbolTable.kindOfMem(letStmtIdentifier), symbolTable.indexOf(letStmtIdentifier));
        }
        parentToken.addChild(letStatement);
        advance();
    }

    private void compileWhile(Token parentToken){
        Token currentTokenVal = previous();
        Token whileStatement = new Token("", TokenType.WHILE_STATEMENT, currentToken);
        whileStatement.addChild( currentTokenVal );

        String thisScopeWhileLabel = labelHandler.advanceWhileLabel();
        vmWriter.writeLabel(thisScopeWhileLabel);

        currentTokenVal = advance();
        if( !currentTokenVal.getTokenType().equals(TokenType.SYMBOL) || !currentTokenVal.getTokenValue().equals("(")){
            error(currentTokenVal.getLine(), "Not a valid while statement");
            return;
        }
        whileStatement.addChild( currentTokenVal );

        advance();
        compileExpr(whileStatement);

        //We always negate our while expression
        vmWriter.writeArithmetic("not");

        currentTokenVal = previous();
        if( !currentTokenVal.getTokenType().equals(TokenType.SYMBOL) || !currentTokenVal.getTokenValue().equals(")")){
            error(currentTokenVal.getLine(), "Not a valid while statement");
            return;
        }
        whileStatement.addChild( currentTokenVal );

        //In our while expression if its true we continue if its not we jump to the end of the while code
        String endWhileLabel = labelHandler.advanceEndWhileLabel();
        vmWriter.writeIf(endWhileLabel);

        currentTokenVal = advance();
        if( !currentTokenVal.getTokenType().equals(TokenType.SYMBOL) || !currentTokenVal.getTokenValue().equals("{")){
            error(currentTokenVal.getLine(), "Not a valid while statement");
            return;
        }
        whileStatement.addChild( currentTokenVal );

        advance();
        Token statements = new Token("", TokenType.STATEMENTS, currentToken);
        compileStatements(statements);
        whileStatement.addChild(statements);

        currentTokenVal = previous();
        if( !currentTokenVal.getTokenType().equals(TokenType.SYMBOL) || !currentTokenVal.getTokenValue().equals("}")){
            error(currentTokenVal.getLine(), "Not a valid while statement");
            return;
        }
        whileStatement.addChild( currentTokenVal );

        parentToken.addChild(whileStatement);
        advance();

        vmWriter.writeGoto(thisScopeWhileLabel);
        vmWriter.writeLabel(endWhileLabel);
    }

    private void compileDo(Token parentToken){
        Token currentTokenVal = previous();
        Token doStatement = new Token("", TokenType.DO_STATEMENT, currentToken);
        doStatement.addChild( currentTokenVal );

        currentTokenVal = advance();
        if( !currentTokenVal.getTokenType().equals(TokenType.IDENTIFIER)){
            error(currentTokenVal.getLine(), "Not a valid do statement");
            return;
        }

        //Checking if the identifier is defined in this scope or the class. If it is it's a method call.
        MemorySegments memSeg = symbolTable.kindOfMem( currentTokenVal.getTokenValue() );

        //If it's a method
        if(!memSeg.equals(MemorySegments.NOT_DEFINED)){

            //We push to the stack the value of the address baseAddress + offset
            //this + index
            vmWriter.writePush(memSeg, symbolTable.indexOf(currentTokenVal.getTokenValue()));

            String identifier = symbolTable.typeOf(currentTokenVal.getTokenValue());
            currentTokenVal.setTokenValue(identifier);
            currentTokenVal.setIdentifierType("method");
        }
        else if (peek().getTokenValue().equals("(")){
            //It's a method call aswell if it doenst contain a "."
            vmWriter.writePush(MemorySegments.POINTER, 0);
            currentTokenVal.setIdentifierType("method");
            currentTokenVal.setTokenValue( symbolTable.getClassName() + "." + currentTokenVal.getTokenValue() );
        }

        doStatement.addChild( currentTokenVal );

        //There can be function() or obj.method()
        currentTokenVal = advance();
        if(currentTokenVal.getTokenValue().equals(".")){
            compileFunctionCall(doStatement);
        }
        else if(currentTokenVal.getTokenValue().equals("(")){
            compileMethodCall(doStatement);
        }

        currentTokenVal = advance();
        if( !currentTokenVal.getTokenType().equals(TokenType.SYMBOL) || !currentTokenVal.getTokenValue().equals(";")){
            error(currentTokenVal.getLine(), "Not a valid do statement");
            return;
        }
        doStatement.addChild( currentTokenVal );

        vmWriter.writePop(MemorySegments.TEMP, 0);

        parentToken.addChild(doStatement);
        advance();
    }

    private void compileReturn(Token parentToken){
        Token currentTokenVal = previous();
        Token retStatement = new Token("", TokenType.RET_STATEMENT, currentToken);
        retStatement.addChild( currentTokenVal );

        //compileExpr(retStatement);
        currentTokenVal = advance();
        if( !currentTokenVal.getTokenValue().equals(";")){
            compileExpr(retStatement);
            currentTokenVal = previous();
        }else {
            vmWriter.writePush(MemorySegments.CONSTANT, 0);
        }

        if( !currentTokenVal.getTokenType().equals(TokenType.SYMBOL) || !currentTokenVal.getTokenValue().equals(";")){
            error(currentTokenVal.getLine(), "Not a valid return statement");
            return;
        }
        retStatement.addChild( currentTokenVal );

        parentToken.addChild(retStatement);
        advance();

        vmWriter.writeReturn();
    }

    private void compileIf(Token parentToken){
        Token currentTokenVal = previous();
        Token ifStatement = new Token("", TokenType.IF_STATEMENT, currentToken);
        ifStatement.addChild( currentTokenVal );

        currentTokenVal = advance();
        if( !currentTokenVal.getTokenType().equals(TokenType.SYMBOL) || !currentTokenVal.getTokenValue().equals("(")){
            error(currentTokenVal.getLine(), "Not a valid if statement");
            return;
        }
        ifStatement.addChild( currentTokenVal );

        advance();
        compileExpr(ifStatement);

        currentTokenVal = previous();
        if( !currentTokenVal.getTokenType().equals(TokenType.SYMBOL) || !currentTokenVal.getTokenValue().equals(")")){
            error(currentTokenVal.getLine(), "Not a valid if statement");
            return;
        }
        ifStatement.addChild( currentTokenVal );

        //Creating our if statement labels to jump to
        String ifTrueScoped = labelHandler.advanceIfTrueLabel();
        String ifFalseScoped = labelHandler.advanceIfFalseLabel();
        String ifEndScoped = labelHandler.advanceIfEndLabel();

        vmWriter.writeIf(ifTrueScoped);

        //If false is our else statement jmp
        vmWriter.writeGoto(ifFalseScoped);

        //If true we will execute the code so we jmp to this label
        vmWriter.writeLabel(ifTrueScoped);

        currentTokenVal = advance();
        if( !currentTokenVal.getTokenType().equals(TokenType.SYMBOL) || !currentTokenVal.getTokenValue().equals("{")){
            error(currentTokenVal.getLine(), "Not a valid if statement");
            return;
        }
        ifStatement.addChild( currentTokenVal );

        advance();
        Token ifInsideStatements = new Token("", TokenType.STATEMENTS, currentToken);
        compileStatements(ifInsideStatements);
        ifStatement.addChild(ifInsideStatements);

        currentTokenVal = previous();
        if( !currentTokenVal.getTokenType().equals(TokenType.SYMBOL) || !currentTokenVal.getTokenValue().equals("}")){
            error(currentTokenVal.getLine(), "Not a valid if statement");
            return;
        }
        ifStatement.addChild( currentTokenVal );

        currentTokenVal = advance();
        if( !currentTokenVal.getTokenType().equals(TokenType.KEYWORD) || !currentTokenVal.getTokenValue().equals("else")){
            //If it was true jump to the of the else, hence we write it here.
            vmWriter.writeLabel(ifFalseScoped);
            parentToken.addChild(ifStatement);
            return;
        }
        ifStatement.addChild( currentTokenVal );

        //If the first statement was true we jump to the end, and dont execute the else
        vmWriter.writeGoto(ifEndScoped);
        vmWriter.writeLabel(ifFalseScoped);

        currentTokenVal = advance();
        if( !currentTokenVal.getTokenType().equals(TokenType.SYMBOL) || !currentTokenVal.getTokenValue().equals("{")){
            error(currentTokenVal.getLine(), "Not a valid else statement");
            return;
        }
        ifStatement.addChild( currentTokenVal );

        advance();
        Token elseInsideStatements = new Token("", TokenType.STATEMENTS, currentToken);
        compileStatements(elseInsideStatements);
        ifStatement.addChild(elseInsideStatements);

        currentTokenVal = previous();
        if( !currentTokenVal.getTokenType().equals(TokenType.SYMBOL) || !currentTokenVal.getTokenValue().equals("}")){
            error(currentTokenVal.getLine(), "Not a valid else statement");
            return;
        }
        ifStatement.addChild( currentTokenVal );

        parentToken.addChild(ifStatement);

        advance();

        vmWriter.writeLabel(ifEndScoped);
    }

    private Token compileExpr(Token parentToken){
        Token currentTokenVal = previous();

        Token expr = new Token("", TokenType.EXPRESSION, currentToken);

        compileTermAlt(expr);
        parentToken.addChild(expr);

        currentTokenVal = previous();
        if(TokenMap.getInstance().containsOperator(currentTokenVal.getTokenValue())){
            expr.addChild(currentTokenVal);
            advance();
            compileTermAlt(expr);

            String command = TokenMap.getInstance().getOperatorCommand(currentTokenVal.getTokenValue());
            vmWriter.writeArithmetic(command);
        }

        currentTokenVal = previous();
        return expr;
    }

    private void compileTermAlt(Token parentToken) {
        Token currentTokenVal = previous();
        Token term = new Token("", TokenType.TERM, currentToken);

        if (currentTokenVal.getTokenType().equals(TokenType.NOT_DEFINED)) {
            error(currentTokenVal.getLine(), "Not a valid term");
            return;
        }


        //If it's a unary operator
        if (currentTokenVal.getTokenValue().equals("-") || currentTokenVal.getTokenValue().equals("~")) {
            term.addChild(currentTokenVal);
            advance();
            compileTermAlt(term);

            if(currentTokenVal.getTokenValue().equals("-")){
                //My map doesn't support the unary op
                vmWriter.writeArithmetic( "neg");
            }else {
                vmWriter.writeArithmetic(TokenMap.getInstance().getOperatorCommand(currentTokenVal.getTokenValue()));
            }
        }
        else if (currentTokenVal.getTokenType().equals(TokenType.IDENTIFIER)) {
            term.addChild(currentTokenVal);
            Token previousToken = currentTokenVal;
            currentTokenVal = advance();
            if (currentTokenVal.getTokenValue().equals("[")) {
                term.addChild(currentTokenVal);
                currentTokenVal = advance();

                //Compiling array expression
                compileExpr(term);

                //Getting expression address (baseArrayAddress + offset)
                vmWriter.writePush(MemorySegments.LOCAL, symbolTable.indexOf(previousToken.getTokenValue()));
                vmWriter.writeArithmetic("add");
                vmWriter.writePop(MemorySegments.POINTER, 1);
                vmWriter.writePush(MemorySegments.THAT, 0);


                currentTokenVal = previous();
                term.addChild(currentTokenVal);
            } else if (currentTokenVal.getTokenValue().equals(".")) {
                compileFunctionCall(term);
                currentTokenVal = previous();
            } else if (currentTokenVal.getTokenValue().equals("(")) {
                term.addChild(currentTokenVal);
                compileMethodCall(term);
                currentTokenVal = advance();
            }
            else{
                parentToken.addChild(term);

                //Pushing the identifier value on the current scope, so it's accessible on the stack
                MemorySegments segment = symbolTable.kindOfMem(previousToken.getTokenValue());
                int indexOfIdentifier = symbolTable.indexOf(previousToken.getTokenValue());

                if(!segment.equals(MemorySegments.NOT_DEFINED)){
                    vmWriter.writePush(segment, indexOfIdentifier);
                }

                return;
            }
        }
        else if(currentTokenVal.getTokenValue().equals("(")){
            term.addChild( currentTokenVal );
            currentTokenVal = advance();
            compileExpr(term);

            currentTokenVal = previous();
            term.addChild(currentTokenVal);
            if(!currentTokenVal.getTokenValue().equals(")")){
                parentToken.addChild(term);
                error(currentTokenVal.getLine(), "No closing for term!");
                return;
            }

        }
        else if(currentTokenVal.getTokenValue().equals("[")){
            currentTokenVal = advance();
            return;
        }
        else {
            term.addChild(currentTokenVal);

            //Check tokentypes different than identifier
            if(currentTokenVal.getTokenType().equals(TokenType.INT_CONST)){
                vmWriter.writePush(MemorySegments.CONSTANT, Integer.parseInt(currentTokenVal.getTokenValue()));
            }
            else if(currentTokenVal.getTokenType().equals(TokenType.STRING_CONST)){
                //Pushing the size of the string to the stack so the String.new method receives it's value
                vmWriter.writePush(MemorySegments.CONSTANT, currentTokenVal.getTokenValue().length() );
                vmWriter.writeCall("String.new", 1);

                for(char strChar : currentTokenVal.getTokenValue().toCharArray()){
                    vmWriter.writePush(MemorySegments.CONSTANT, (int)strChar);
                    vmWriter.writeCall("String.appendChar", 2);
                }
            }
            else if(currentTokenVal.getTokenValue().equals("true")){
                vmWriter.writePush(MemorySegments.CONSTANT, 0);
                vmWriter.writeArithmetic("not");
            }
            else if(currentTokenVal.getTokenValue().equals("false")){
                vmWriter.writePush(MemorySegments.CONSTANT, 0);
            }
            else if(currentTokenVal.getTokenValue().equals("this")){
                vmWriter.writePush(MemorySegments.POINTER, 0);
            }
            else if(currentTokenVal.getTokenValue().equals("null")){
                vmWriter.writePush(MemorySegments.CONSTANT, 0);
            }

        }

        if(TokenMap.getInstance().containsOperator(currentTokenVal.getTokenValue())){
            parentToken.addChild(term);
            return;
        }

        currentTokenVal = advance();
        parentToken.addChild(term);
    }

    private void compileExprList(Token parentToken){
        Token currentTokenVal = previous();
        if( !currentTokenVal.getTokenValue().equals(")")){
            compileExpr(parentToken);
            currentTokenVal = previous();
            //parentToken.addChild( currentTokenVal );

            if(currentTokenVal.getTokenValue().equals(",")){
                parentToken.addChild( currentTokenVal );
                advance();
                compileExprList(parentToken);
            }
            else if(currentTokenVal.getTokenValue().equals("(")){
                parentToken.addChild( currentTokenVal );
                advance();
                compileExprList(parentToken);
            }

        }
    }

    private void compileFunctionCall(Token parentToken){
        Token currentTokenVal = previous();
        parentToken.addChild( currentTokenVal );

        currentTokenVal = advance();
        if( !currentTokenVal.getTokenType().equals(TokenType.IDENTIFIER)){
            error(currentTokenVal.getLine(), "Not a valid function call");
            return;
        }
        parentToken.addChild( currentTokenVal );

        currentTokenVal = advance();
        compileMethodCall(parentToken);
    }

    private void compileMethodCall(Token parentToken){
        Token currentTokenVal = previous();
        if( !currentTokenVal.getTokenType().equals(TokenType.SYMBOL) || !currentTokenVal.getTokenValue().equals("(")){
            error(currentTokenVal.getLine(), "Not a valid invoke statement");

            return;
        }
        parentToken.addChild( currentTokenVal );

        Token expressionList = new Token("", TokenType.EXPRESSION_LIST, currentToken);
        currentTokenVal = advance();
        compileExprList(expressionList);
        parentToken.addChild(expressionList);

        currentTokenVal = previous();
        if( !currentTokenVal.getTokenType().equals(TokenType.SYMBOL) || !currentTokenVal.getTokenValue().equals(")")){
            error(currentTokenVal.getLine(), "Not a valid invoke statement");
            return;
        }
        parentToken.addChild( currentTokenVal );

        Stmt.CallStmt stmt = new Stmt.CallStmt(parentToken);
        MemorySegments memSeg = symbolTable.kindOfMem( stmt.getFirstIdentifierValue() );

        if(!memSeg.equals(MemorySegments.NOT_DEFINED)){
            stmt.setMethod();
            vmWriter.writePush(MemorySegments.THIS, symbolTable.indexOf(stmt.getFirstIdentifierValue()));
            stmt.setFirstIdentifierValue( symbolTable.typeOf(stmt.getFirstIdentifierValue()) );
        }

        vmWriter.writeCall(stmt.getCallableStmt(), stmt.getnArgs());
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

    private void error(int line, String message) {
        hadError = true;
        report(line, filename, message);
    }
    private void report(int line, String where, String message) {
        System.err.println("[line " + line + "] Error: " + where + ": " + message);
    }
}
