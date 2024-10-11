package gusev.dmitry.research.net.sockets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Gusev Dmitry (gusevd)
 * @version 1.0 (DATE: 20.09.12)
*/

public class SocketDeviceServer {

 public static void main(String[] args) {
  ServerSocket serverSocket = null;
  Socket         client    = null;
  BufferedReader instream  = null;
  OutputStream   outstream;

  try {
   // Create a server socket
   serverSocket = new ServerSocket(3000);
   System.out.println("Waiting for a quote request");

   // Wait for a first request
   client = serverSocket.accept();
   // Get the streams
   instream = new BufferedReader(new InputStreamReader(client.getInputStream()));
   outstream = client.getOutputStream();
   // write data - system alive
   //outstream.write("          ALIVE_DATA".getBytes());
   //System.out.println("Alive symbols sent.");

   // starting "send alive" thread
   /*
   SendAliveMessage aliveMessage = new SendAliveMessage(outstream);
   Thread aliveThread = new Thread(aliveMessage);
   aliveThread.start();
   aliveThread.join();
   System.out.println("Send alive thread started.");
   */
   // permanent data sending

   while (true) {
    //client = serverSocket.accept();
    //instream = new BufferedReader(new InputStreamReader(client.getInputStream()));
    //outstream = client.getOutputStream();
    double random = Math.random();
    System.out.println(random);
    int delay = (int) (random*20) + 1;
    System.out.println("Delay = " + delay + " second(s).");

    outstream.write("          ALIVE_DATA".getBytes());
    System.out.println("ALIVE_DATA was sended.");
    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    String date = sdf.format(new Date());
    outstream.write(date.getBytes());
    System.out.println("Date " + date + " was sended. Delay (" + delay + ").\n");

    try {
     Thread.sleep(delay*1000);
    } catch (InterruptedException e) {
     System.err.println(e.getMessage());
    }
   }

  } catch (IOException ioe) {
   System.out.println("Error in Server: " + ioe);
  } /*catch (InterruptedException e) {
   System.out.println(e.getMessage());
  } */finally {
   try {
    instream.close();
    //outstream.close();
   } catch(Exception e) {
    System.out.println("StockQuoteServer: can’t close streams"
    + e.getMessage());
    }
    }
 }

 /***/
 static class SendAliveMessage implements Runnable {

  private OutputStream outstream;

  SendAliveMessage(OutputStream outstream) {
   this.outstream = outstream;
  }

  @Override
  public void run() {
   while(true) {
    try {
     outstream.write("          ALIVE_DATA".getBytes());
     System.out.println("Alive signal was sended.");
     Thread.sleep(5000);
    } catch (IOException e) {
     System.err.println(e.getMessage());
    } catch (InterruptedException e) {
     System.err.println(e.getMessage());
    }
   }
  }

 }

}