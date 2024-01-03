package main.CodeGeneration;

import main.CompileEngine.TokenCategory;
import main.Tokenizer.Token;
import main.Tokenizer.TokenType;

import java.util.HashMap;
import java.util.Map;

/**
 * Generates symbol tables for a jack class and it's subroutines.
 * This symbol table will contain all the class variables.
 * In methods the subroutine symbol table will also include a this argument
 * as the first argument.
 * We only need 2 symbol tables. A class table and a subroutine table.
 *
 * Implementation tips:
 * Use two separate hash tables. One for class another for subroutine.
 * Reset the subroutine hashtable for each compilation of subroutine tables.
 * If there is non identified symbols they can be class names or subroutine names.
 *
 * */
public class SymbolTable {
    private String className;
    private String subroutineName;
    private Map<String, TableEntry> tableValues;
    private Map<String, TableEntry> subroutineTable;

    public SymbolTable(){
        this.tableValues = new HashMap<>();
    }

    /* Resets subroutine symbol table and starts a new one */
    public void startSubroutine(Token tokenObj){
        this.subroutineName = tokenObj.getTokenValue();
        this.subroutineTable = new HashMap<>();
    }

    /* Used to define a new identifier. Name, type, kind and number (#).
    * The number is the running index of the identifier.
    * Identifiers can be: class scoped (field and static)
    *                     subroutine scoped (arg and var)
    *  */
    public void define(Token tokenObj){

        Map<String, TableEntry> tempMap = null;

        if(tokenObj.getTokenCategory().equals(TokenCategory.STATIC)
                || tokenObj.getTokenCategory().equals(TokenCategory.FIELD)){
            tempMap = tableValues;
        }else if (tokenObj.getTokenCategory().equals(TokenCategory.ARGUMENT)
                || tokenObj.getTokenCategory().equals(TokenCategory.VAR)){
            tempMap = subroutineTable;
        } else if (tokenObj.getTokenCategory().equals(TokenCategory.CLASS)) {
            className = tokenObj.getTokenValue();
            return;
        }

        if(tempMap == null){
            return;
        }

       long tableNumber = tempMap.values().stream()
                .filter(arg -> arg.getCategory().equals(tokenObj.getTokenCategory()))
               .count();

        tempMap.put(tokenObj.getTokenValue(), new TableEntry(tokenObj, tableNumber));
    }

    /* Returns the number of variables of a given kind, defined in the current scope */
    public int varCount(TokenCategory category){
        long subroutineVarCount = subroutineTable.values().stream()
                .filter(arg ->arg.getCategory().equals(category)).count();

        long classVarCount = tableValues.values().stream()
                .filter(arg ->arg.getCategory().equals(category)).count();

        return (int)(subroutineVarCount + classVarCount);
    }

    /* Returns the kind of the named identifier. Returns NONE if not found */
    public TokenCategory kindOf(String identifierName){
        TableEntry tableEntry = subroutineTable.get(identifierName);

        //Checking the outer scope symbol table
        if (tableEntry == null){
            tableEntry = tableValues.get(identifierName);
        }

        return tableEntry != null ? tableEntry.getCategory() : TokenCategory.NOT_DEFINED;
    }

    /* Returns the kind of the named identifier. Returns NONE if not found */
    public MemorySegments kindOfMem(String identifierName){
        TableEntry tableEntry = subroutineTable.get(identifierName);

        //Checking the outer scope symbol table
        if (tableEntry == null){
            tableEntry = tableValues.get(identifierName);
        }

        if(tableEntry == null){
            return MemorySegments.NOT_DEFINED;
        }

        if(tableEntry.getCategory().equals(TokenCategory.VAR)){
            return MemorySegments.LOCAL;
        }
        else if(tableEntry.getCategory().equals(TokenCategory.STATIC)){
            return MemorySegments.STATIC;
        }
        else if(tableEntry.getCategory().equals(TokenCategory.ARGUMENT)){
            return MemorySegments.ARGUMENT;
        }
        else if(tableEntry.getCategory().equals(TokenCategory.FIELD)){
            return MemorySegments.THIS;
        }
        else {
            return MemorySegments.TEMP;
        }


    }

    /* Returns the type of the named identifier. */
    public String typeOf(String identifierName){
        TableEntry tableEntry = subroutineTable.get(identifierName);

        //Checking the outer scope symbol table
        if (tableEntry == null){
            tableEntry = tableValues.get(identifierName);
        }

        return tableEntry != null ? tableEntry.getType() : "NONE";
    }

    /* Returns the index (#) of the named identifier. */
    public int indexOf(String identifierName){
        TableEntry tableEntry = subroutineTable.get(identifierName);

        //Checking the outer scope symbol table
        if (tableEntry == null){
            tableEntry = tableValues.get(identifierName);
        }

        return tableEntry != null ? (int)tableEntry.getNumber() : 0;
    }

    public String getSubroutinePath(){
        return className + "." + subroutineName;
    }

    public String getClassName() {
        return className;
    }

    @Override
    public String toString() {
        return "SymbolTable{" +
                "Class Table = " + tableValues +
                ", Subroutine Table = " + subroutineTable +
                '}';
    }
}
