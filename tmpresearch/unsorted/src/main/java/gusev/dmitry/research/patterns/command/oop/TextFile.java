package gusev.dmitry.research.patterns.command.oop;

public class TextFile {

    private String name;

    public TextFile(String name) {
        this.name = name;
    }

    // constructor

    public String open() {
        return "Opening file " + name;
    }

    public String save() {
        return "Saving file " + name;
    }

    // additional text file methods (editing, writing, copying, pasting)

}