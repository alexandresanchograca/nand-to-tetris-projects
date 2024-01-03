package main.CodeGeneration;

public enum MemorySegments {
    NOT_DEFINED,
    CONSTANT, //only used to push to the stack obviously
    LOCAL,
    STATIC,
    THIS,
    THAT,
    POINTER,
    TEMP,
    ARGUMENT;

    @Override
    public String toString() {
        return super.toString().toLowerCase() + " ";
    }
}
