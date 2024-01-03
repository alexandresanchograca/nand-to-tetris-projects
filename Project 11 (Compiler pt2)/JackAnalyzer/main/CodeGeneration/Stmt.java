package main.CodeGeneration;

import main.Tokenizer.Token;
import main.Tokenizer.TokenType;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class Stmt {
    //Statements can be let, do, if or while
    public static class CallStmt {
        private List<Token> identifiers;
        private int nArgs;
        private boolean isMethod;

        public CallStmt(Token doStatementToken){
            this.identifiers = new LinkedList<>();
            this.isMethod = false;
            tokenToObj(doStatementToken);
        }

        private void tokenToObj(Token tokenObj){
            for(Token childToken : tokenObj.getChilds()){

                if(childToken.getTokenType().equals(TokenType.IDENTIFIER)){
                    this.identifiers.add(childToken);
                }

                else if(childToken.getTokenType().equals(TokenType.EXPRESSION_LIST)){
                    this.nArgs = (int)childToken.getChilds().stream()
                            .filter(child -> child.getTokenType().equals(TokenType.EXPRESSION))
                            .count();
                }
            }

            if(identifiers.size() > 0){
                if( identifiers.getFirst().getIdentifierType().equals("method")){
                    isMethod = true;
                    nArgs++;
                }
            }
        }

        public void setMethod(){
            if(!isMethod){
                    isMethod = true;
                    nArgs++;
            }
        }

        public String getFirstIdentifierValue(){
            return identifiers.getFirst().getTokenValue();
        }

        public void setFirstIdentifierValue(String value){
            identifiers.getFirst().setTokenValue(value);
        }

        public String getCallableStmt(){
            return identifiers.stream()
                    .map(token -> token.getTokenValue())
                    .collect(Collectors.joining("."));
        }

        public int getnArgs() {
            return nArgs;
        }
    }


    public static class IfStmt extends Stmt {



    }
}
