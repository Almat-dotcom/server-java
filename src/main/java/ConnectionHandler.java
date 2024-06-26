import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ConnectionHandler implements Runnable {
    private Socket clientSocket;

    public ConnectionHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            InputStream inputStream = clientSocket.getInputStream();
            BufferedReader bufferedReader =
                    new BufferedReader(new InputStreamReader(inputStream));
            String startLine = bufferedReader.readLine();
            String[] startLineArr = startLine.split(" ");
            String output;
            if (startLineArr[0].equals("GET")) {
                if (startLineArr[1].equals("/")) {
                    output = "HTTP/1.1 200 OK \r\n\r\n";
                } else if (startLineArr[1].startsWith("/echo/")) {
                    String content = startLineArr[1].replace("/echo/", "");
                    output = "HTTP/1.1 200 OK \r\n"
                            + "Content-Type: text/plain\r\n"
                            + "Content-Length: " + content.length() + " \r\n\r\n" +
                            content;
                } else if (startLineArr[1].equals("/user-agent")) {
                    String userAgentHeader = "";
                    while ((userAgentHeader = bufferedReader.readLine()) != null) {
                        if (userAgentHeader.startsWith("User-Agent")) {
                            break;
                        }
                    }
                    assert userAgentHeader != null;
                    String[] userAgentHeaderArr = userAgentHeader.split(" ");
                    output = "HTTP/1.1 200 OK \r\n"
                            + "Content-Type: text/plain\r\n"
                            + "Content-Length: " + userAgentHeaderArr[1].length() +
                            " \r\n\r\n" + userAgentHeaderArr[1] + "\r\n\r\n";
                } else if (startLineArr[1].startsWith("/files/")) {
                    String fileName = startLineArr[1].replace("/files/", "");
                    Path path = Paths.get(DirectoryStore.getDirectory(), fileName);
                    if (Files.exists(path)) {
                        String fileContent = new String(Files.readAllBytes(path));
                        output = "HTTP/1.1 200 OK \r\n"
                                + "Content-Type: application/octet-stream\r\n"
                                + "Content-Length: " + fileContent.length() + " \r\n\r\n" +
                                fileContent;
                    } else {
                        output = "HTTP/1.1 404 Not Found \r\n"
                                + "Content-Length: 0 \r\n\r\n";
                    }
                } else {
                    output = "HTTP/1.1 404 Not Found \r\n\r\n";
                }
                clientSocket.getOutputStream().write(
                        (output).getBytes(StandardCharsets.UTF_8));
            } else if (startLineArr[0].equals("POST")) {
                if (startLineArr[1].startsWith("/files/")) {
                    String fileName = startLineArr[1].replace("/files", "");
                    File file = new File(DirectoryStore.getDirectory() + fileName);
                    if (!file.exists()) {
                        if (file.createNewFile()) {
                            System.out.println("New file created");
                        }
                    }
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        if (line.isEmpty()) {
                            break;
                        }
                    }
                    StringBuilder content = new StringBuilder();
                    while (bufferedReader.ready()) {
                        content.append((char) bufferedReader.read());
                    }
                    PrintWriter writer = new PrintWriter(file);
                    writer.print(content.toString());
                    writer.close();
                    clientSocket.getOutputStream().write(
                            ("HTTP/1.1 201 Created \r\n\r\n")
                                    .getBytes(StandardCharsets.UTF_8));
                }
            }
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }
}
