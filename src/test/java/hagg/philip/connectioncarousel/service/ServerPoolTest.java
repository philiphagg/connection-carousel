package hagg.philip.connectioncarousel.service;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ServerPoolTest {

    @Test
    void testServerPool() {
        ServiceInstance serviceInstance = new ServiceInstance(null);
        ServerPool serverPool = new ServerPool(serviceInstance);
        assertNotNull(serverPool);
    }

    @Test
    void testServerPool_multiple() {
        ServiceInstance firstServiceInstance = new ServiceInstance(null);
        ServiceInstance SecondServiceInstance = new ServiceInstance(null);
        ServerPool serverPool = new ServerPool(firstServiceInstance, SecondServiceInstance);
        assertNotNull(serverPool);
        assertEquals(2, serverPool.getInstances().size());
    }

    @Test
    void nextIndex() {
        ServiceInstance firstServiceInstance = new ServiceInstance(null);
        ServiceInstance secondServiceInstance = new ServiceInstance(null);
        ServerPool serverPool = new ServerPool(firstServiceInstance, secondServiceInstance);

        assertEquals(0, serverPool.getCurrent());
        serverPool.nextInstance();
        assertEquals(1, serverPool.getCurrent());
        serverPool.nextInstance();
        assertEquals(0, serverPool.getCurrent());
    }

    @Test
    @SneakyThrows
    void getNextPeer() {
        ServiceInstance firstServiceInstance = new ServiceInstance(URI.create("http://www.google.se").toURL());
        ServiceInstance secondServiceInstance = new ServiceInstance(URI.create("http://www.facebook.se").toURL());
        ServiceInstance expected = new ServiceInstance(URI.create("http://www.amazon.se").toURL());

        secondServiceInstance.setAlive(false);
        ServerPool serverPool = new ServerPool(firstServiceInstance, secondServiceInstance, expected);

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

        ServerPool serverPool = new ServerPool(firstServiceInstance, secondServiceInstance);
        serverPool.markAsDead(secondServiceInstance);

        assertTrue(firstServiceInstance.isAlive());
        assertFalse(secondServiceInstance.isAlive());
    }

    @Test
    @SneakyThrows
    void healthCheck() {
        ServiceInstance first = new ServiceInstance(URI.create("http://www.google.com").toURL());
        ServiceInstance second = new ServiceInstance(URI.create("http://www.facebook.com").toURL());
        ServiceInstance third = new ServiceInstance(URI.create("http://www.amazon.com").toURL());

        Map<ServiceInstance, Boolean> mockPingResults = new HashMap<>();
        mockPingResults.put(first, true);
        mockPingResults.put(second, false);
        mockPingResults.put(third, true);

        TestableServerPool serverPool = new TestableServerPool(mockPingResults, first, second, third);

        serverPool.halthCheck();

        assertTrue(first.isAlive());
        assertFalse(second.isAlive());
        assertTrue(third.isAlive());
    }

    @Test
    @SneakyThrows
    void testLoadBalancing() {
        ServiceInstance first = new ServiceInstance(new URL("http://www.google.com"));
        ServiceInstance second = new ServiceInstance(new URL("http://www.facebook.com"));
        ServiceInstance third = new ServiceInstance(new URL("http://www.amazon.com"));

        second.setAlive(false); // Simulate that Facebook is down

        ServerPool serverPool = new ServerPool(first, second, third);

        HttpRequest request = new HttpRequest("/path");
        HttpResponse response = new HttpResponse();

        serverPool.roundRobinBalance(request, response);

        assertEquals(200, response.getStatusCode());
        assertEquals("Hello from http://www.amazon.com", response.getBody());
    }
}