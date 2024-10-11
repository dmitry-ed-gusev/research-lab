package gusev.dmitry.research.books.java24h_trainer.lesson18.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

/**
 * @author Gusev Dmitry (gusevd)
 * @version 1.0 (DATE: 01.11.12)
 */

public class WebSiteReader {

    public static void main(String args[]) {
        String nextLine;
        URL url = null;
        URLConnection urlConn = null;
        InputStreamReader inStream = null;
        BufferedReader buff = null;
        try {

            // adding system proxy server - first option
            //System.setProperty("http.proxyHost", "http://proxy.mycompany_1.com");
            //System.setProperty("http.proxyPort", String.valueOf(8080));

            // adding system proxy - second option
            Proxy myProxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("http://proxy.mycompany.com", 8080));

            // index.html is a default URL’s file name
            url = new URL("http://www.google.com");

            //urlConn = url.openConnection();            // don't use proxy for connection
            urlConn = url.openConnection(myProxy);  // using proxy for connection

            inStream = new InputStreamReader(urlConn.getInputStream(), "UTF8");
            buff = new BufferedReader(inStream);
            // Read and print the lines from index.html
            while (true) {
                nextLine = buff.readLine();
                if (nextLine != null) {
                    System.out.println(nextLine);
                } else {
                    break;
                }
            }
        } catch (MalformedURLException e) {
            System.out.println("Please check the URL: " + e.toString());
        } catch (IOException e1) {
            System.out.println("Can't read from the Internet: " + e1.toString());
        } finally {
            if (inStream != null) {
                try {
                    inStream.close();
                    buff.close();
                } catch (IOException e1) {
                    System.out.println("Can't close the streams: " + e1.getMessage());
                }
            }
        }
    }

}