import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ThreadedEchoHandler implements Runnable {
    private Socket socket;

    ThreadedEchoHandler(Socket socket){
        this.socket = socket;
    }

    @Override
    public void run() {
        try(    InputStream inStream = socket.getInputStream();
                OutputStream outStream = socket.getOutputStream()){

            Scanner scanner = new Scanner(inStream, "UTF-8");
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(outStream, "UTF-8"), true);

            writer.println("Hello!");

            while(scanner.hasNextLine()){
                String line = scanner.nextLine();
                writer.println(line);
                if(line.trim().equals("BYE"))
                    break;
            }

            while(scanner.hasNextLine()){
                System.out.println(scanner.nextLine());
            }

        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
