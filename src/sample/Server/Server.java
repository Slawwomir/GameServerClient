package sample.Server;

import javafx.scene.control.TextArea;
import sample.Player.Player;
import sample.Player.PlayerProperties;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Server implements Runnable {
    public static final int MAX_CLIENTS = 3;

    private int connectedClients;
    private TextArea textArea;
    private List<Player> players;
    private ExecutorService executor;

    public Server(TextArea textArea) {
        players = new ArrayList<>(2);
        players.add(new Player());
        players.add(new Player());
        this.textArea = textArea;
        executor = Executors.newFixedThreadPool(2);
    }

    @Override
    public void run() {
        try(ServerSocket s = new ServerSocket(9090)){

            textArea.appendText("Server has been successfully initialized. Waiting for clients...\n");
            while(connectedClients < MAX_CLIENTS){
                Socket incoming = s.accept();
                textArea.appendText("New client [" + connectedClients + "] connected\n");
                players.add(new Player());
                Runnable r = new ThreadedHandler(this, incoming, textArea, connectedClients);
                executor.execute(r);
                //Thread t = new Thread(r);
                //t.start();
                connectedClients++;
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public boolean ifTwoPlayersAreConnected(){
        return connectedClients == 2;
    }

    public List<Player> getPlayers(){
        return players;
    }
}

class ThreadedHandler implements Runnable {
    private Server server;
    private Socket socket;
    private TextArea textArea;
    private int identity;

    ThreadedHandler(Server server, Socket socket, TextArea textArea, int identity){
        this.server = server;
        this.socket = socket;
        this.textArea = textArea;
        this.identity = identity;
    }

    @Override
    public void run() {
        try(InputStream inStream = socket.getInputStream();
            OutputStream outStream = socket.getOutputStream()){

            //Scanner scanner = new Scanner(inStream, "UTF-8");
            //PrintWriter writer = new PrintWriter(new OutputStreamWriter(outStream, "UTF-8"), true);
            textArea.appendText("[" + identity + "] Waiting for second player\n");

            ObjectInputStream objectInputStream = new ObjectInputStream(inStream);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outStream);

            textArea.appendText("[" + identity + "] Successfully connected!\n");

            while(true){
                try {
                    PlayerProperties incoming = (PlayerProperties) objectInputStream.readObject();
                    server.getPlayers().get(identity).update(incoming);
                    //while(!server.ifTwoPlayersAreConnected());
                    objectOutputStream.writeObject(server.getPlayers().get(identity ^ 1).getProperties());
                } catch (ClassNotFoundException | IOException e){
                    e.printStackTrace();
                    break;
                }
            }
            /*while(scanner.hasNextLine()){
                String line = scanner.nextLine();
                textArea.appendText("[" + identity + "]" + line + "\n");
                if(line.trim().equals("BYE"))
                    break;
            }*/
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}


