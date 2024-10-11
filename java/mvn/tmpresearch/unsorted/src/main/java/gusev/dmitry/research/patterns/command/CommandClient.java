package gusev.dmitry.research.patterns.command;

public class CommandClient {

    public static void main(String[] args) {

        // OOP approach
        TextFileOperationExecutor textFileOperationExecutorOOP
                = new TextFileOperationExecutor();
        textFileOperationExecutorOOP.executeOperation(
                new OpenTextFileOperation(new TextFile("file1.txt")));
        textFileOperationExecutorOOP.executeOperation(
                new SaveTextFileOperation(new TextFile("file2.txt")));

        // functional approach
        TextFileOperationExecutor textFileOperationExecutorF
                = new TextFileOperationExecutor();
        textFileOperationExecutorF.executeOperation(() -> "Opening file file1.txt");
        textFileOperationExecutorF.executeOperation(() -> "Saving file file1.txt");

        // functional with methods references
        TextFileOperationExecutor textFileOperationExecutor
                = new TextFileOperationExecutor();
        TextFile textFile = new TextFile("file1.txt");
        textFileOperationExecutor.executeOperation(textFile::open);
        textFileOperationExecutor.executeOperation(textFile::save);

    }

}
