import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class SocketTest {

    public static void main(String... args) throws IOException {
        try (Socket s = new Socket("127.0.0.1", 8189);
             Scanner in = new Scanner(s.getInputStream(), "UTF-8"))
        {
            PrintWriter out = new PrintWriter(new OutputStreamWriter(s.getOutputStream(), "UTF-8"), true);
            while(in.hasNextLine()){
                String line = in.nextLine();
                out.println(line);
                System.out.println(line);
            }
        }
    }
}
