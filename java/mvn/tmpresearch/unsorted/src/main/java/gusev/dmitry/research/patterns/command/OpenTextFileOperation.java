package gusev.dmitry.research.patterns.command;

public class OpenTextFileOperation implements TextFileOperation {

    private TextFile textFile;

    // constructors
    public OpenTextFileOperation(TextFile textFile) {
        this.textFile = textFile;
    }

    @Override
    public String execute() {
        return textFile.open();
    }

}