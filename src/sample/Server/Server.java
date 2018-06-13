package sample.Server;

import javafx.scene.control.TextArea;
import sample.GameState;
import sample.Player.PlayerProperties;

import java.awt.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Runnable {
    public static final int MAX_CLIENTS = 2;

    private int connectedClients;
    private TextArea textArea;
    private List<PlayerProperties> players;
    private ExecutorService executor;
    private GameState gameState;
    private boolean change;

    public Server(TextArea textArea) {
        gameState = new GameState();
        players = gameState.getPlayersProperties();
        this.textArea = textArea;
        executor = Executors.newFixedThreadPool(2);
    }

    @Override
    public void run() {
        try(ServerSocket s = new ServerSocket(9191)){

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

    public synchronized boolean ifTwoPlayersAreConnected(){
        return connectedClients == 2;
    }

    public List<PlayerProperties> getPlayersProperties(){
        return players;
    }

    public GameState getGameState() {
        return gameState;
    }

    public synchronized boolean isChange() {
        return change;
    }

    public synchronized void setChange(boolean change) {
        this.change = change;
    }
}

class ThreadedHandler implements Runnable {
    private Server server;
    private Socket socket;
    private TextArea textArea;
    private int identity;
    private final static Object lock = new Object();
    private final static Object queue = new Object();
    private List<Point> toTurnOff;
    private Thread readThread;

    ThreadedHandler(Server server, Socket socket, TextArea textArea, int identity){
        this.server = server;
        this.socket = socket;
        this.textArea = textArea;
        this.identity = identity;
        toTurnOff = new ArrayList<>();
    }

    @Override
    public void run() {
        try(InputStream inStream = socket.getInputStream();
            OutputStream outStream = socket.getOutputStream()){

            //textArea.appendText("[" + identity + "] Waiting for second player\n");

            ObjectInputStream objectInputStream = new ObjectInputStream(inStream);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outStream);

            //textArea.appendText("[" + identity + "] Successfully connected!\n");

            while(!server.ifTwoPlayersAreConnected());

            objectOutputStream.writeObject(server.getGameState().getPlayersProperties().get(identity));
            objectOutputStream.writeObject(server.getGameState().getPlayersProperties().get(identity^1));
            objectOutputStream.writeObject(server.getGameState());

            new java.util.Timer().scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    synchronized (lock) {
                        Arrays.stream(server.getGameState().getBoardProperties().getElements())
                                .flatMap(Arrays::stream)
                                .filter(el -> el.getDelayTime() > 0).forEach(el -> {
                            el.setDelayTime(Math.max(el.getDelayTime() - 100, 0));
                            if (el.getDelayTime() == 0)
                                el.setUsed(false);

                        });

                        int k = (int) Arrays.stream(server.getGameState().getBoardProperties().getElements())
                                .flatMap(Arrays::stream)
                                .filter(el -> el.getSign() == 'o' && !el.isUsed()).count();
                        k -= 1;
                        server.getGameState().increaseWaterLevel(k * 0.5);
                        server.setChange(true);
                    }
                }
            }, 500, 200);

            readThread = new Thread(new Read(server, objectInputStream,lock,identity));
            readThread.start();

            GameState state, in;
            while(true){
                if(server.isChange()) {
                    synchronized (lock) {
                        GameState out = new GameState(server.getGameState());
                        objectOutputStream.writeObject(out);
                        server.setChange(false);
                    }
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}

class Read implements Runnable {

    private Server server;
    private ObjectInputStream inputStream;
    private Object lock;
    private int identity;

    public Read(Server server, ObjectInputStream inputStream, Object lock, int identity) {
        this.server = server;
        this.inputStream = inputStream;
        this.lock = lock;
        this.identity = identity;
    }

    @Override
    public void run() {
        GameState in;
        GameState state;
        while(true){
            try {
                in = (GameState) inputStream.readObject();

                synchronized (lock) {
                    state = server.getGameState();
                    server.setChange(!state.equals(in) || !state.getPlayersProperties().equals(in.getPlayersProperties()));
                    state.updateOne(in, identity);
                    if (in.isChangesInElements()) {
                        //state.setBoardProperties(in.getBoardProperties());
                        state.updateChanged(in);
                    }
                    lock.notifyAll();
                }

            } catch (ClassNotFoundException | IOException e){
                e.printStackTrace();
                break;
            }
        }
    }
}

