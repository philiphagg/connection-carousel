package hagg.philip.connectioncarousel.service;

public class HttpRequest {
    private final String path;

    public HttpRequest(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

}
