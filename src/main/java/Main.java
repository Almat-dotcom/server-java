import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

public class Main {
    private static final String USER_AGENT_HEADER_KEY = "User-Agent";

    public static void main(String[] args) {
        System.out.println("Logs from your program will appear here!");


        ServerSocket serverSocket = null;
        Socket clientSocket = null;

        try {
            serverSocket = new ServerSocket(4221);
            serverSocket.setReuseAddress(true);
            while (true) {
                clientSocket = serverSocket.accept();
                System.out.println("Accepted new connection");
                ConnectionHandler connectionHandler =
                        new ConnectionHandler(clientSocket);
                new Thread(connectionHandler).start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
