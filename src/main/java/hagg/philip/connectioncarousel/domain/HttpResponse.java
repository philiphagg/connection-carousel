package hagg.philip.connectioncarousel.domain;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class HttpResponse {
    private int statusCode;
    private String body;

    @Override
    public String toString() {
        return "HttpResponse{" +
                "statusCode=" + statusCode +
                ", body='" + body + '\'' +
                '}';
    }
}
