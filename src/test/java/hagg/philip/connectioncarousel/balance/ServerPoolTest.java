package hagg.philip.connectioncarousel.balance;

import hagg.philip.connectioncarousel.balance.strategy.LoadBalancingStrategy;
import hagg.philip.connectioncarousel.balance.strategy.RoundRobinStrategy;
import hagg.philip.connectioncarousel.domain.HttpRequest;
import hagg.philip.connectioncarousel.domain.HttpResponse;
import hagg.philip.connectioncarousel.domain.ServiceInstance;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Description;

import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ServerPoolTest {

    @Test
    void testServerPool() {
        ServiceInstance serviceInstance = new ServiceInstance(null);
        LoadBalancingStrategy strategy = new RoundRobinStrategy();  // Inject the strategy
        ServerPool serverPool = new ServerPool(List.of(serviceInstance), List.of(strategy));
        assertNotNull(serverPool);
    }

    @Test
    void testServerPool_multiple() {
        ServiceInstance firstServiceInstance = new ServiceInstance(null);
        ServiceInstance SecondServiceInstance = new ServiceInstance(null);
        LoadBalancingStrategy strategy = new RoundRobinStrategy();
        ServerPool serverPool = new ServerPool(List.of(firstServiceInstance, SecondServiceInstance), List.of(strategy));
        assertNotNull(serverPool);
        assertEquals(2, serverPool.getInstances().size());
    }

    @Test
    @SneakyThrows
    void getNextPeer() {
        ServiceInstance firstServiceInstance = new ServiceInstance(URI.create("http://www.google.se").toURL());
        ServiceInstance secondServiceInstance = new ServiceInstance(URI.create("http://www.facebook.se").toURL());
        ServiceInstance expected = new ServiceInstance(URI.create("http://www.amazon.se").toURL());

        secondServiceInstance.setAlive(false);
        LoadBalancingStrategy strategy = new RoundRobinStrategy();
        ServerPool serverPool = new ServerPool(List.of(firstServiceInstance, secondServiceInstance, expected), List.of(strategy));

        ServiceInstance nextPeer = serverPool.getNextPeer();
        assertEquals(nextPeer, expected);

        ServiceInstance result = serverPool.getNextPeer();
        assertEquals(result, firstServiceInstance);
    }

    @Test
    @SneakyThrows
    void markAsDead() {
        ServiceInstance firstServiceInstance = new ServiceInstance(URI.create("http://www.google.se").toURL());
        ServiceInstance secondServiceInstance = new ServiceInstance(URI.create("http://www.facebook.se").toURL());

        LoadBalancingStrategy strategy = new RoundRobinStrategy();
        ServerPool serverPool = new ServerPool(List.of(firstServiceInstance, secondServiceInstance), List.of(strategy));
        serverPool.markAsDead(secondServiceInstance);

        assertTrue(firstServiceInstance.isAlive());
        assertFalse(secondServiceInstance.isAlive());
    }



    @Test
    @SneakyThrows
    @Description("Test that the load balancing works as expected")
    void testLoadBalancing() {
        ServiceInstance first = new ServiceInstance(new URL("http://www.google.com"));
        ServiceInstance second = new ServiceInstance(new URL("http://www.facebook.com"));
        ServiceInstance third = new ServiceInstance(new URL("http://www.amazon.com"));

        second.setAlive(false); // Simulate that Facebook is down

        LoadBalancingStrategy strategy = new RoundRobinStrategy();
        ServerPool serverPool = new ServerPool(List.of(first, second, third), List.of(strategy));
        HttpRequest request = new HttpRequest("/path");
        HttpResponse response = new HttpResponse();

        serverPool.balanceRequest(request, response);

        assertEquals(200, response.getStatusCode());
        assertEquals("Hello from http://www.amazon.com", response.getBody());
    }

    @Test
    @SneakyThrows
    void testRetryLogic() {
        ServiceInstance first = new ServiceInstance(new URL("http://www.google.com"));
        ServiceInstance second = new ServiceInstance(new URL("http://www.facebook.com"));
        ServiceInstance third = new ServiceInstance(new URL("http://www.amazon.com"));

        first.setAlive(false);
        second.setAlive(false);

        LoadBalancingStrategy strategy = new RoundRobinStrategy();
        ServerPool serverPool = new ServerPool(List.of(first, second, third), List.of(strategy));

        HttpRequest request = new HttpRequest("/path");
        HttpResponse response = new HttpResponse();

        serverPool.balanceRequest(request, response);

        // Expecting the third backend (Amazon) to serve the request
        assertEquals(200, response.getStatusCode());
        assertTrue(response.toString().contains("amazon.com"));
    }

    @Test
    @SneakyThrows
    void testMaxRetriesExceeded() {
        ServiceInstance first = new ServiceInstance(new URL("http://www.google.com"));
        ServiceInstance second = new ServiceInstance(new URL("http://www.facebook.com"));
        ServiceInstance third = new ServiceInstance(new URL("http://www.amazon.com"));

        // Simulate all backends being down
        first.setAlive(false);
        second.setAlive(false);
        third.setAlive(false);

        LoadBalancingStrategy strategy = new RoundRobinStrategy();
        ServerPool serverPool = new ServerPool(List.of(first, second, third), List.of(strategy));

        HttpRequest request = new HttpRequest("/path");
        HttpResponse response = new HttpResponse();

        serverPool.balanceRequest(request, response);


        assertEquals(503, response.getStatusCode());
        assertEquals("Service not available after retries", response.getBody());
    }

    @Test
    void testNoBackendsAvailable() {
        LoadBalancingStrategy strategy = new RoundRobinStrategy();
        ServerPool serverPool = new ServerPool(List.of(strategy));

        HttpRequest request = new HttpRequest("/path");
        HttpResponse response = new HttpResponse();

        serverPool.balanceRequest(request, response);

        // Expecting 503 Service Unavailable due to no backends
        assertEquals(503, response.getStatusCode());
        assertEquals("No backends available", response.getBody());
    }

    @Test
    @SneakyThrows
    void testDynamicBackendAddition() {
        LoadBalancingStrategy strategy = new RoundRobinStrategy();
        ServerPool serverPool = new ServerPool(List.of(strategy));


        HttpRequest request = new HttpRequest("/path");
        HttpResponse response = new HttpResponse();

        serverPool.balanceRequest(request, response);
        assertEquals(503, response.getStatusCode());


        ServiceInstance first = new ServiceInstance(new URL("http://www.google.com"));
        serverPool.addInstance(first);
        response = new HttpResponse();
        serverPool.balanceRequest(request, response);


        assertEquals(200, response.getStatusCode());
    }
}