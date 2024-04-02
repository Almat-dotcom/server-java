import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

public class Main {
    static String directoryPath = "";

    public static void main(String[] args) {
        System.out.println("Logs from your program will appear here!");


        Socket clientSocket = null;

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("--directory")) {
                DirectoryStore.setDirectory(args[++i]);
                break;
            }
        }

        try (ServerSocket serverSocket = new ServerSocket(4221)) {
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
