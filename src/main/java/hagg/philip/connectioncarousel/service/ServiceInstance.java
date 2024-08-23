package hagg.philip.connectioncarousel.service;

import java.net.URL;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ServiceInstance {
    URL url;
    boolean isAlive;
    final ReentrantReadWriteLock lock;
    Object reverseProxy;

    public ServiceInstance(URL url) {
        this.url = url;
        this.isAlive = true;
        this.lock = new ReentrantReadWriteLock();
        this.reverseProxy = null;
    }

    public void setAlive(boolean alive) {
        lock.writeLock().lock();
        try {
            this.isAlive = alive;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public boolean isAlive() {
        lock.readLock().lock();
        try {
            System.out.println("isAlive: " + this.isAlive);
            return this.isAlive == true;
        } finally {
            lock.readLock().unlock();
        }
    }

    public boolean serve(HttpRequest request, HttpResponse response) {
        lock.readLock().lock();
        try {
            if (isAlive()) {
                System.out.println("Serving request: " + request.getPath());
                response.setStatusCode(200);
                response.setBody("Hello from " + url);
                return true;
            } else {
                System.out.println("Service is dead: " + url);
                response.setStatusCode(503);
                response.setBody("Service is dead: " + url);
                return false;
            }
        } finally {
            lock.readLock().unlock();
        }
    }
}
