package dmitry.gusev.marklogic.client.example.cookbook;

import com.marklogic.client.DatabaseClientFactory.Authentication;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Utilities to support and simplify examples.
 */
public class Util {
    /**
     * ExampleProperties represents the configuration for the examples.
     */
    static public class ExampleProperties {
        public String host;
        public int port = -1;
        public String adminUser;
        public String adminPassword;
        public String readerUser;
        public String readerPassword;
        public String writerUser;
        public String writerPassword;
        public Authentication authType;

        public ExampleProperties(Properties props) {
            super();
            host = props.getProperty("example.host");
            port = Integer.parseInt(props.getProperty("example.port"));
            adminUser = props.getProperty("example.admin_user");
            adminPassword = props.getProperty("example.admin_password");
            readerUser = props.getProperty("example.reader_user");
            readerPassword = props.getProperty("example.reader_password");
            writerUser = props.getProperty("example.writer_user");
            writerPassword = props.getProperty("example.writer_password");
            authType = Authentication.valueOf(
                    props.getProperty("example.authentication_type").toUpperCase()
            );
        }
    }

    /**
     * Read the configuration properties for the example.
     *
     * @return the configuration object
     */
    public static ExampleProperties loadProperties() throws IOException {
        String propsName = "Example.properties";

        InputStream propsStream = openStream(propsName);
        if (propsStream == null)
            throw new IOException("Could not read properties " + propsName);

        Properties props = new Properties();
        props.load(propsStream);

        return new ExampleProperties(props);
    }

    /**
     * Read a resource for an example.
     *
     * @param fileName the name of the resource
     * @throws IOException
     * @return an input stream for the resource
     */
    public static InputStream openStream(String fileName) throws IOException {
        return Util.class.getClassLoader().getResourceAsStream(fileName);
    }
}
