package gusev.dmitry.research.patterns.command;

public class TextFile {

    private String name;

    public TextFile(String name) {
        this.name = name;
    }

    // constructor

    public String open() {
        System.out.println("Opening file " + name);
        return "Opening file " + name;
    }

    public String save() {
        System.out.println("Saving file " + name);
        return "Saving file " + name;
    }

    // additional text file methods (editing, writing, copying, pasting)

}