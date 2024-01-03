package main.CodeGeneration;

public enum SubroutineSig {
    CONSTRUCTOR,
    METHOD,
    FUNCTION;

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}
