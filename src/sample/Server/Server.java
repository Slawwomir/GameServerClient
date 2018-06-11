package sample.Server;

import javafx.scene.control.TextArea;
import sample.GameState;
import sample.Player.Player;
import sample.Player.PlayerProperties;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Runnable {
    public static final int MAX_CLIENTS = 2;

    private int connectedClients;
    private TextArea textArea;
    private List<PlayerProperties> players;
    private ExecutorService executor;
    private GameState gameState;

    public Server(TextArea textArea) {
        gameState = new GameState();
        players = gameState.getPlayersProperties();
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
                //players.add(new Player());
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

    public List<PlayerProperties> getPlayersProperties(){
        return players;
    }

    public GameState getGameState() {
        return gameState;
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

            textArea.appendText("[" + identity + "] Waiting for second player\n");

            ObjectInputStream objectInputStream = new ObjectInputStream(inStream);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outStream);

            textArea.appendText("[" + identity + "] Successfully connected!\n");

            PlayerProperties initial = server.getGameState().getPlayersProperties().get(identity);
            objectOutputStream.writeObject(initial);

            initial = server.getGameState().getPlayersProperties().get(identity^1);
            objectOutputStream.writeObject(initial);

            objectOutputStream.writeObject(server.getGameState());
            while(true){
                try {
                    /*
                    //while(!server.ifTwoPlayersAreConnected());
                    PlayerProperties dowyslania = server.getGameState().getPlayersProperties().get(identity ^ 1);
                    objectOutputStream.writeObject(new PlayerProperties(dowyslania.x, dowyslania.y));
                    PlayerProperties odebrane = (PlayerProperties) objectInputStream.readObject();
                    server.getGameState().getPlayersProperties().get(identity).update(odebrane);
                    //System.out.println(identity + ". wysylam " + dowyslania.x + "." + dowyslania.y + " | Odbieram " + odebrane.x + "." + odebrane.y);
                    */
                    GameState state = server.getGameState();
                    objectOutputStream.writeObject(new GameState(state));
                    System.out.println(identity + "-0: " + server.getGameState().getPlayersProperties().get(0).x + "|" + server.getGameState().getPlayersProperties().get(0).y +
                            " --- 1 " + server.getGameState().getPlayersProperties().get(1).x + "|" + server.getGameState().getPlayersProperties().get(1).y);
                    GameState in = (GameState) objectInputStream.readObject();
                    server.getGameState().updateOne(in, identity);

                } catch (ClassNotFoundException | IOException e){
                    e.printStackTrace();
                    break;
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}


