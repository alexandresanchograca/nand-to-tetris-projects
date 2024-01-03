package main.CompileEngine;

import main.Tokenizer.Token;

/**
 * Class to handle Statements, called Stmt because
 * it's a widely used expression in compilers.
 *
 * This class is used to create data structures for our
 * parse tree / syntax tree
 * */
public class Stmt {

    public static class classDeclaration{

        public Token keyword;
        public Token identifier;
        public Token leftBracket;
        public Token rightBracket;

        public classDeclaration(Token keyword, Token identifier, Token leftBracket){
            this.keyword = keyword;
            this.identifier = identifier;
            this.leftBracket = leftBracket;
        }
    }


}
