package hagg.philip.connectioncarousel.balance.api;

import hagg.philip.connectioncarousel.balance.ServerPool;
import hagg.philip.connectioncarousel.domain.HttpRequest;
import hagg.philip.connectioncarousel.domain.HttpResponse;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class Controller {

    private final ServerPool serverPool;

    @GetMapping("/load-balance")
    public ResponseEntity<String> loadBalance(@RequestParam String path) {
        HttpRequest request = new HttpRequest(path);
        HttpResponse response = new HttpResponse();

        serverPool.balanceRequest(request, response);

        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    }

}
