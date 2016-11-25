package dmitry.gusev.marklogic.client.example.cookbook;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.InputStreamHandle;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * DocumentDelete illustrates how to delete a database document.
 */
public class DocumentDelete {
    public static void main(String[] args) throws IOException {
        run(Util.loadProperties());
    }

    public static void run(Util.ExampleProperties props) throws IOException {
        System.out.println("example: " + DocumentDelete.class.getName());

        String filename = "flipper.xml";

        // create the client
        DatabaseClient client = DatabaseClientFactory.newClient(
                props.host, props.port, props.writerUser, props.writerPassword,
                props.authType);

        // create a manager for XML documents
        XMLDocumentManager docMgr = client.newXMLDocumentManager();

        // create an identifier for the document
        String docId = "/example/" + filename;

        setUpExample(docMgr, docId, filename);

        // delete the document
        docMgr.delete(docId);

        System.out.println("Deleted the /example/" + filename + " document");

        // release the client
        client.release();
    }

    // set up by writing document content for the example to delete
    public static void setUpExample(XMLDocumentManager docMgr, String docId, String filename) throws IOException {
        InputStream docStream = Util.openStream("data" + File.separator + filename);
        if (docStream == null)
            throw new IOException("Could not read document example");

        InputStreamHandle handle = new InputStreamHandle();
        handle.set(docStream);

        docMgr.write(docId, handle);
    }
}
