import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class HttpRequest {
    private static final String KEY_VALUE_DIVISOR = ": ";
    private static final String SPACE_DIVISOR = " ";

    private String method;
    private String path;
    private String protocol;

    private Map<String, String> headers;

    public HttpRequest(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader =
                new BufferedReader(new InputStreamReader(inputStream));
        String line = bufferedReader.readLine();

        if (line == null) {
            return;
        }

        processMethodPathAndProtocol(line);

        while ((line = bufferedReader.readLine()) != null) {
            if (line.isEmpty()) {
                break;
            }
            processHeader(line);
        }
    }

    private void processMethodPathAndProtocol(String line) {
        String[] lineSplitted = line.split(SPACE_DIVISOR);

        this.method = lineSplitted[0];
        this.path = lineSplitted[1];
        this.protocol = lineSplitted[2];
    }

    private void processHeader(String line) {
        String[] lineSplitted = line.split(KEY_VALUE_DIVISOR);

        if (headers == null) {
            headers = new HashMap<>();
        }

        String headerKey = lineSplitted[0];
        String headerValue = lineSplitted[1];

        headers.put(headerKey, headerValue);
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getHeader(String key){
        return headers.get(key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(method, path, protocol, headers);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        HttpRequest that = (HttpRequest) obj;
        return Objects.equals(method, that.method) &&
                Objects.equals(path, that.path) &&
                Objects.equals(protocol, that.protocol) &&
                Objects.equals(headers, that.headers);
    }
}
