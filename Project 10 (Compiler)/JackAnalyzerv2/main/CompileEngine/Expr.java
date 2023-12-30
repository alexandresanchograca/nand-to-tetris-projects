package main.CompileEngine;

import main.Tokenizer.Token;
import main.Tokenizer.TokenTypeEnum;

/**
 * Class to handle Expressions, called Expr because
 * it's a widely used expression in compilers.
 *
 * This class is used to create data structures for our
 * parse tree / syntax tree
 * */
public abstract class Expr {
    public static class Binary{

        private String firstExpr;
        private Token operand;
        private String secondExpr;

        public Binary(String firstExpr, Token operand, String secondExpr){
            this.firstExpr = firstExpr;
            this.operand = operand;
            this.secondExpr = secondExpr;
        }


    }

    public static class Unary{

        private String firstExpr;
        private Token operand;

        public Unary(Token operand, String firstExpr){
            this.firstExpr = firstExpr;
            this.operand = operand;
        }

    }
}
