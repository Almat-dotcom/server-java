import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
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
            clientSocket = serverSocket.accept();
            HttpRequest httpRequest = new HttpRequest(clientSocket.getInputStream());
            if (matches(httpRequest, "/", "GET")) {
                byte[] bytes = handleSimpleRequest(httpRequest);
                writeOutputStream(clientSocket, bytes);
            } else if (matchesStartsWith(httpRequest, "/echo", "GET")) {
                byte[] bytes = handleEchoRequest(httpRequest);
                writeOutputStream(clientSocket, bytes);
            } else if (matches(httpRequest, "/user-agent", "GET")) {
                byte[] bytes = handleUserAgentRequest(httpRequest);
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

    private static boolean matchesStartsWith(HttpRequest httpRequest, String pathPrefix, String method) {
        boolean pathMathces = httpRequest.getPath() != null &&
                httpRequest.getPath().startsWith(pathPrefix);
        boolean methodMatches = method.equals(httpRequest.getMethod());
        return pathMathces && methodMatches;
    }

    private static boolean matches(HttpRequest httpRequest, String path, String method) {
        boolean pathMathces = path.equals(httpRequest.getPath());
        boolean methodMatches = method.equals(httpRequest.getMethod());
        return pathMathces && methodMatches;
    }

    private static byte[] handleUserAgentRequest(HttpRequest httpRequest) {
        String userAgent = httpRequest.getHeader(USER_AGENT_HEADER_KEY);
        HttpResponse httpResponse = new HttpResponse(200, "OK");
        httpResponse.setBody(userAgent);

        String output = httpResponse.make();
        System.out.println("The output :: ");
        System.out.println(output);
        return output.getBytes(StandardCharsets.UTF_8);
    }

    private static void writeOutputStream(Socket socket, byte[] bytes)
            throws IOException {
        socket.getOutputStream().write(bytes);
    }
}
