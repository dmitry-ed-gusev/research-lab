package dmitry.gusev.marklogic.client.example.cookbook;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.InputStreamHandle;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * DocumentWriter illustrates how to write content to a database document.
 */
public class DocumentWrite {
    public static void main(String[] args) throws IOException {
        run(Util.loadProperties());
    }

    public static void run(Util.ExampleProperties props) throws IOException {
        System.out.println("example: " + DocumentWrite.class.getName());

        // create the client
        DatabaseClient client = DatabaseClientFactory.newClient(
                props.host, props.port, props.writerUser, props.writerPassword,
                props.authType);

        // use either shortcut or strong typed IO
        runShortcut(client);
        runStrongTyped(client);

        tearDownExample(client);

        // release the client
        client.release();
    }

    public static void runShortcut(DatabaseClient client) throws IOException {
        String filename = "flipper.xml";

        // acquire the content
        InputStream docStream = Util.openStream("data" + File.separator + filename);
        if (docStream == null)
            throw new IOException("Could not read document example");

        // create a manager for XML documents
        XMLDocumentManager docMgr = client.newXMLDocumentManager();

        // create an identifier for the document
        String docId = "/example/" + filename;

        // write the document content
        docMgr.writeAs(docId, docStream);

        System.out.println("(Shortcut) Wrote /example/" + filename + " content");
    }

    public static void runStrongTyped(DatabaseClient client) throws IOException {
        String filename = "flipper.xml";

        // acquire the content
        InputStream docStream = Util.openStream("data" + File.separator + filename);
        if (docStream == null)
            throw new IOException("Could not read document example");

        // create a manager for XML documents
        XMLDocumentManager docMgr = client.newXMLDocumentManager();

        // create an identifier for the document
        String docId = "/example/" + filename;

        // create a handle on the content
        InputStreamHandle handle = new InputStreamHandle();
        handle.set(docStream);

        // write the document content
        docMgr.write(docId, handle);

        System.out.println("(Strong Typed) Wrote /example/" + filename + " content");
    }

    // clean up by deleting the document that the example wrote
    public static void tearDownExample(DatabaseClient client) {
        XMLDocumentManager docMgr = client.newXMLDocumentManager();

        String docId = "/example/flipper.xml";

        docMgr.delete(docId);
    }
}
