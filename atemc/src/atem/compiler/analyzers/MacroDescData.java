package atem.compiler.analyzers;

public class MacroDescData {
    public final String macroAnnotationValue;
    public final String[] macroValueArray;

    public MacroDescData(String macroAnnotationValue)
    {
        this.macroAnnotationValue = macroAnnotationValue;
        this.macroValueArray = macroAnnotationValue.split(" ");
    }

    public int size()
    {
        return  macroValueArray.length;
    }
}
