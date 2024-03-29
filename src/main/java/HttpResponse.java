import java.util.HashMap;
import java.util.Map;

public class HttpResponse {

    private static final String LINE_DIVISOR = "\r\n";

    private String protocol = "HTTP/1.1";
    private int status;
    private String statusCode;

    private Map<String, String> headers;
    private String body;

    public HttpResponse(int status, String statusCode) {
        this.status = status;
        this.statusCode = statusCode;
        headers = new HashMap<>();
    }

    public HttpResponse setProtocol(String protocol) {
        this.protocol = protocol;
        return this;
    }

    public HttpResponse setStatus(int status) {
        this.status = status;
        return this;
    }

    public HttpResponse setStatusCode(String statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    public HttpResponse addHeaders(String key, String value) {
        this.headers.put(key, value);
        return this;
    }

    public HttpResponse setBody(String body) {
        this.body = body;
        return this;
    }

    public String make() {
        StringBuilder output =
                new StringBuilder("%s %s %s".formatted(protocol, status, statusCode));
        output.append(LINE_DIVISOR);

        for (Map.Entry<String, String> header : headers.entrySet()) {
            output.append("%s %s".formatted(header.getKey(), header.getValue()));
            output.append(LINE_DIVISOR);
        }

        if (body != null) {
            output.append("Content-Type: %s".formatted("text/plain"));
            output.append(LINE_DIVISOR);

            output.append("Content-Length: %s".formatted(body.length()));
            output.append(LINE_DIVISOR);

            output.append(LINE_DIVISOR);
            output.append(body);
        }

        output.append(LINE_DIVISOR + LINE_DIVISOR);
        return output.toString();
    }
}
