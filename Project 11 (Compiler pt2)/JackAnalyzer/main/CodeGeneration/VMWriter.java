package main.CodeGeneration;

import main.CompileEngine.TokenCategory;
import main.Tokenizer.Token;
import main.Tokenizer.TokenType;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Used to generate VM code from Jack Code (our high level language).
 *
 *
 * */
public class VMWriter {
    private final FileWriter fileWriter;

    /* Creates a new output .vm file and writes to it */
    public VMWriter(String filename) throws IOException {
        this.fileWriter = new FileWriter(filename.substring(0, filename.indexOf(".")) + ".vm");
    }

    /* Writes a vm push command to a virtual memory segment
    * Virtual Mem Segments are:
    * constant, arg, local, static, this, that, pointer temp
    * */
    public void writePush(MemorySegments segment, int index){
        writeCommand("push " + segment + index);
    }

    /* Writes a vm pop command to a virtual memory segment
     * Virtual Mem Segments are:
     * constant, arg, local, static, this, that, pointer temp
     * */
    public void writePop(MemorySegments segment, int index) {
        writeCommand("pop " + segment.toString() + index);
    }

    /* Writes a arithmetic command.
    * these commands are:
    * add, sub, neg, eq, gt, lt , and, or, not
    * */
    public void writeArithmetic(String command)  {
           writeCommand(command);
    }

    public void writeGoto(String label) {
        writeCommand("goto " + label);
    }

    public void writeIf(String label) {
        writeCommand("if-goto " + label);
    }

    //Problably can put a Stmt type as argument
    public void writeCall(String fName, int nArgs){
        writeCommand("call " + fName + " " + nArgs);
    }

    public void writeFunction(String fName, int nLocals){
        writeCommand("function " + fName + " " + nLocals);
    }

    public void writeReturn() {
            writeCommand("return");
    }

    public void writeLabel(String labelName){
        writeCommand("label " + labelName);
    }

    /* Closing output file */
    public void close() throws IOException {
        fileWriter.close();
    }

    private void writeCommand(String command){
        try{
            fileWriter.write(command + "\n");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
