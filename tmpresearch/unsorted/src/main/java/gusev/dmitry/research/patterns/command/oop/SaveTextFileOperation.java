package gusev.dmitry.research.patterns.command.oop;

public class SaveTextFileOperation implements TextFileOperation {

    private TextFile textFile;

    // same field and constructor as above
    public SaveTextFileOperation(TextFile textFile) {
        this.textFile = textFile;
    }

    @Override
    public String execute() {
        return textFile.save();
    }

}