package dgusev.net.sockets.client;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

/**
 * @author Gusev Dmitry (gusevd)
 * @version 1.0 (DATE: 13.10.2014)
 */

public class TCServiceClient {

    private static Log log = LogFactory.getLog(TCServiceClient.class);

    private String host;
    private int port;

    public TCServiceClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void sendData() {
        String tcFile = "test_telegrams.txt";
        //Socket      socket             = null;
        PrintStream outToServerWriter = null;

        Scanner scanner = new Scanner(System.in);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(TCServiceClient.class.getClassLoader().getResourceAsStream(tcFile), "windows-1251"));
             Socket socket = new Socket(this.host, this.port)) {
            //try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("c:/temp/test_tc.txt"), "windows-1251"))) {

            log.info("connected!");
            outToServerWriter = new PrintStream(socket.getOutputStream());

            String line;
            while ((line = reader.readLine()) != null) {

                //System.out.println(line.length());

                //char ch = line.charAt(1);
                //if (ch == 0xFEFF) {
                //    System.out.println("!!!");
                //}

                if (!StringUtils.isBlank(line)) {
                    System.out.printf("client data -> [%s] [%s]\n", line.length(), line);
                    outToServerWriter.print(line);
                    outToServerWriter.flush();
                    System.out.println("client data sent!");
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        log.error(e);
                    }
                }

                scanner.nextLine();
                //break;
            }

        } catch (IOException e) {
            log.error(e);
        }

    }


}