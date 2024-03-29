import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) {
        System.out.println("Logs from your program will appear here!");

        ServerSocket serverSocket = null;
        Socket clientSocket = null;

        try {
            serverSocket = new ServerSocket(4221);
            serverSocket.setReuseAddress(true);
            clientSocket = serverSocket.accept();
            HttpRequest httpRequest = new HttpRequest(clientSocket.getInputStream());
            if (httpRequest.getPath().equals("/")) {
                byte[] bytes = handleSimpleRequest(httpRequest);
                writeOutputStream(clientSocket, bytes);
            } else if (httpRequest.getPath() != null &&
                    httpRequest.getPath().startsWith("/echo")) {
                byte[] bytes = handleEchoRequest(httpRequest);
                writeOutputStream(clientSocket, bytes);
            } else {
                byte[] bytes = handleNotFoundRequest(httpRequest);
                writeOutputStream(clientSocket, bytes);
            }
            System.out.println("accepted new connection");
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }

    private static byte[] handleEchoRequest(HttpRequest httpRequest) {
        String body = httpRequest.getPath().split("/echo/")[1];

        HttpResponse httpResponse = new HttpResponse(200, "OK");
        httpResponse.setBody(body);

        String output = httpResponse.make();
        System.out.println("The output :: ");
        System.out.println(output);
        return output.getBytes(StandardCharsets.UTF_8);
    }

    private static byte[] handleSimpleRequest(HttpRequest httpRequest) {
        return "HTTP/1.1 200 OK\r\n\r\n".getBytes(StandardCharsets.UTF_8);
    }

    private static byte[] handleNotFoundRequest(HttpRequest httpRequest) {
        return "HTTP/1.1 404 Not Found\r\n\r\n".getBytes(StandardCharsets.UTF_8);
    }

    private static void writeOutputStream(Socket socket, byte[] bytes)
            throws IOException {
        socket.getOutputStream().write(bytes);
    }
}
