package sample.Server;

import javafx.scene.control.TextArea;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server implements Runnable {
    public static final int MAX_CLIENTS = 3;

    private int connectedClients;
    private TextArea textArea;

    public Server(TextArea textArea) {
        this.textArea = textArea;
    }

    @Override
    public void run() {
        try(ServerSocket s = new ServerSocket(9090)){

            textArea.appendText("Server has been successfully initialized. Waiting for clients...\n");
            while(connectedClients < MAX_CLIENTS){
                Socket incoming = s.accept();
                connectedClients++;
                textArea.appendText("New client [" + connectedClients + "] connected\n");
                Runnable r = new ThreadedHandler(incoming, textArea, connectedClients);
                Thread t = new Thread(r);
                t.start();
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}

class ThreadedHandler implements Runnable {
    private Socket socket;
    private TextArea textArea;
    private int identity;

    ThreadedHandler(Socket socket, TextArea textArea, int identity){
        this.socket = socket;
        this.textArea = textArea;
        this.identity = identity;
    }

    @Override
    public void run() {
        try(    InputStream inStream = socket.getInputStream();
                OutputStream outStream = socket.getOutputStream()){

            Scanner scanner = new Scanner(inStream, "UTF-8");
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(outStream, "UTF-8"), true);

            textArea.appendText("[" + identity + "] Successfully connected!\n");

            while(scanner.hasNextLine()){
                String line = scanner.nextLine();
                textArea.appendText("[" + identity + "]" + line + "\n");
                if(line.trim().equals("BYE"))
                    break;
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}


