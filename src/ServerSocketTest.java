import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class ServerSocketTest {

    public static void main(String... args) throws IOException {

        try(ServerSocket s = new ServerSocket(8189)){

            while(true){
                Socket incoming = s.accept();
                System.out.println("New client connected");
                Runnable r = new ThreadedEchoHandler(incoming);
                Thread t = new Thread(r);
                t.start();
            }
        }
    }
}
