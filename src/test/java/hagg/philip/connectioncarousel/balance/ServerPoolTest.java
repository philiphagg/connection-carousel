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
        ServerPool serverPool = new ServerPool(List.of(serviceInstance), strategy);
        assertNotNull(serverPool);
    }

    @Test
    void testServerPool_multiple() {
        ServiceInstance firstServiceInstance = new ServiceInstance(null);
        ServiceInstance SecondServiceInstance = new ServiceInstance(null);
        LoadBalancingStrategy strategy = new RoundRobinStrategy();
        ServerPool serverPool = new ServerPool(List.of(firstServiceInstance, SecondServiceInstance), strategy);
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
        ServerPool serverPool = new ServerPool(List.of(firstServiceInstance, secondServiceInstance, expected), strategy);

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
        ServerPool serverPool = new ServerPool(List.of(firstServiceInstance, secondServiceInstance), strategy);
        serverPool.markAsDead(secondServiceInstance);

        assertTrue(firstServiceInstance.isAlive());
        assertFalse(secondServiceInstance.isAlive());
    }

    @Test
    @SneakyThrows
    @Description(
            "Test that the health check works as expected; due to the random nature of the implementation, " +
            "a less random class was introduced to ensure that the test is deterministic"
    )
    void healthCheck() {
        ServiceInstance first = new ServiceInstance(URI.create("http://www.google.com").toURL());
        ServiceInstance second = new ServiceInstance(URI.create("http://www.facebook.com").toURL());
        ServiceInstance third = new ServiceInstance(URI.create("http://www.amazon.com").toURL());

        Map<ServiceInstance, Boolean> mockPingResults = new HashMap<>();
        mockPingResults.put(first, true);
        mockPingResults.put(second, false);
        mockPingResults.put(third, true);

        LoadBalancingStrategy strategy = new RoundRobinStrategy();
        TestableServerPool serverPool = new TestableServerPool(mockPingResults,strategy, first, second, third);

        serverPool.halthCheck();

        assertTrue(first.isAlive());
        assertFalse(second.isAlive());
        assertTrue(third.isAlive());
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
        ServerPool serverPool = new ServerPool(List.of(first, second, third), strategy);
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
        ServerPool serverPool = new ServerPool(List.of(first, second, third), strategy);

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
        ServerPool serverPool = new ServerPool(List.of(first, second, third), strategy);

        HttpRequest request = new HttpRequest("/path");
        HttpResponse response = new HttpResponse();

        serverPool.balanceRequest(request, response);


        assertEquals(503, response.getStatusCode());
        assertEquals("Service not available after retries", response.getBody());
    }

    @Test
    void testNoBackendsAvailable() {
        LoadBalancingStrategy strategy = new RoundRobinStrategy();
        ServerPool serverPool = new ServerPool(strategy);

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
        ServerPool serverPool = new ServerPool(strategy);


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