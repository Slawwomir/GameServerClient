import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class SocketTest {

    public static void main(String... args) throws IOException {
        try (   Socket s = new Socket("time-a.nist.gov", 13);
                Scanner in = new Scanner(s.getInputStream(), "UTF-8"))
        {
            while(in.hasNextLine()){
                System.out.println(in.nextLine());
            }
        }
    }
}
