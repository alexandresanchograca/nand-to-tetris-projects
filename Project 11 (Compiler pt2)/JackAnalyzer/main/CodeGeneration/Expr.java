package main.CodeGeneration;

import main.Tokenizer.Token;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public abstract class Expr {

    private Token term;

    public Expr(Token term){
        this.term = term;
    }

    public static class Binary extends Expr{
        private String operator;
        private Token secondTerm;

        public Binary(Token firstTerm, String operator, Token secondTerm){
            super(firstTerm);
            this.operator = operator;
            this.secondTerm = secondTerm;
        }

        public String getOperator() {
            return operator;
        }

        public Token getSecondTerm() {
            return secondTerm;
        }
    }

    public static class Unary extends Expr{
        private String operator;

        public Unary(String operator, Token term){
            super(term);
            this.operator = operator;
        }

        public String getOperator() {
            return operator;
        }
    }

    public static class SimpleExpr extends Expr{
        public SimpleExpr(Token term){
            super(term);
        }
    }

    public static class SubroutineCall extends Expr{

        private final List<Expr> exprList;

        public SubroutineCall(Expr ...args){
            super(args[0].getTerm());
            this.exprList = new LinkedList<>();
            initList(args);
        }

        private void initList(Expr ...args){
            exprList.addAll(Arrays.asList(args));
        }

    }

    public Token getTerm() {
        return term;
    }
}
