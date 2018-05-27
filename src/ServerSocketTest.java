import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class ServerSocketTest {

    public static void main(String... args) throws IOException {

        try(ServerSocket s = new ServerSocket(8189)){

            try(Socket incoming = s.accept()){

                InputStream inStream = incoming.getInputStream();
                OutputStream outStream = incoming.getOutputStream();

                try(Scanner in = new Scanner(inStream, "UTF-8")){

                    PrintWriter out = new PrintWriter(new OutputStreamWriter(outStream, "UTF-8"), true);

                    out.println("Success");

                    while(in.hasNextLine()){
                        out.println("Echo: " + in.nextLine());
                    }
                }
            }
        }
    }
}
