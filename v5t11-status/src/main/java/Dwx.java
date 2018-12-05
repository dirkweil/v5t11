import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Dwx {
  public static void main(String[] args) throws UnknownHostException, IOException {
    String host = args[0];
    int port = Integer.parseInt(args[1]);
    try (Socket socket = new Socket(host, port)) {
      System.out.println("Connection established to " + host + ":" + port);
    }
  }

}
