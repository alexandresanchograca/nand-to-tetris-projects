package main.CodeGeneration;

/**
 * Used to handle labels
 * */
public class Label {
    private int whileStmtCounter;
    private int whileEndStmtCounter;
    private int ifTrueStmtCounter;
    private int ifFalseStmtCounter;
    private int ifEndStmtCounter;


    public Label(){
        this.whileStmtCounter = 0;
        this.whileEndStmtCounter = 0;
        this.ifTrueStmtCounter = 0;
        this.ifFalseStmtCounter = 0;
        this.ifEndStmtCounter = 0;
    }

    public void resetLabels(){
        this.whileStmtCounter = 0;
        this.whileEndStmtCounter = 0;
        this.ifTrueStmtCounter = 0;
        this.ifFalseStmtCounter = 0;
        this.ifEndStmtCounter = 0;
    }



    public String advanceWhileLabel() {
        return "WHILE_EXP" + whileStmtCounter++;
    }

    public String advanceEndWhileLabel() {
        return "WHILE_END" + whileEndStmtCounter++;
    }

    public String advanceIfTrueLabel() {
        return "IF_TRUE" + ifTrueStmtCounter++;
    }

    public String advanceIfFalseLabel() {
        return "IF_FALSE" + ifFalseStmtCounter++;
    }

    public String advanceIfEndLabel() {
        return "IF_END" + ifEndStmtCounter++;
    }

    public String previousIfEndLabel() {
        return "IF_END" + (ifEndStmtCounter - 1);
    }


    public String previousWhileLabel(){
        return "WHILE_EXP" + (whileStmtCounter - 1);
    }

    public String previousEndWhileLabel(){
        return "WHILE_EXP" + (whileEndStmtCounter - 1);
    }
}
