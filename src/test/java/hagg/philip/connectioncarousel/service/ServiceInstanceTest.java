package hagg.philip.connectioncarousel.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ServiceInstanceTest {

    @org.junit.jupiter.api.Test
    void testServiceInstance() {
        ServiceInstance serviceInstance = new ServiceInstance(null);
        assertNotNull(serviceInstance);
    }

    @Test
    void testSetAlive() {
        ServiceInstance serviceInstance = new ServiceInstance(null);
        serviceInstance.setAlive(true);
        assertTrue(serviceInstance.isAlive);
    }

    @Test
    void testSetAliveFalse() {
        ServiceInstance serviceInstance = new ServiceInstance(null);
        serviceInstance.setAlive(false);
        assertFalse(serviceInstance.isAlive);
    }

    @Test
    void testIsAlive() {
        ServiceInstance serviceInstance = new ServiceInstance(null);
        assertTrue(serviceInstance.isAlive());
    }

    @Test
    void testIsAliveFalse() {
        ServiceInstance serviceInstance = new ServiceInstance(null);
        serviceInstance.setAlive(false);
        assertFalse(serviceInstance.isAlive());
    }


}