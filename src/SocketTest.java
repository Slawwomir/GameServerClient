import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class SocketTest {

    public static void main(String... args) throws IOException {
        try (Socket s = new Socket("127.0.0.1", 8189);
             Scanner in = new Scanner(s.getInputStream(), "UTF-8"))
        {
            while(in.hasNextLine()){
                System.out.println(in.nextLine());
            }
        }
    }
}
