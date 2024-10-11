package gusev.dmitry.research.net.sockets;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class TcpIpClientTest {

    /**
     * @param args
     */
    public static void main(String[] args) {

        try {
            Socket s = new Socket("172.18.53.102", 10002);
            PrintStream out = new PrintStream(s.getOutputStream());
            out.println("Hello Mr Kim!");
            System.out.println("Message was sent!");
            //OutputStream outbound = s.getOutputStream();
            //outbound.write("Privet, Mr kim!".getBytes());

        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
