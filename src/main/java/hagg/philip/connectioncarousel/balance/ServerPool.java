package hagg.philip.connectioncarousel.balance;

import hagg.philip.connectioncarousel.balance.strategy.LoadBalancingStrategy;
import hagg.philip.connectioncarousel.domain.HttpRequest;
import hagg.philip.connectioncarousel.domain.HttpResponse;
import hagg.philip.connectioncarousel.domain.ServiceInstance;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class ServerPool {
    private final int MAX_ATTEMPTS = 3;

    @Getter
    List<ServiceInstance> instances;
    private final LoadBalancingStrategy strategy;

    public ServerPool(List<ServiceInstance> instances, LoadBalancingStrategy strategy) {
        this.instances = instances;
        this.strategy = strategy;
    }

    public ServerPool(LoadBalancingStrategy strategy, ServiceInstance... instances) {
        this(Arrays.asList(instances), strategy);
    }

    public ServerPool(LoadBalancingStrategy strategy) {
        this.instances = new ArrayList<>();
        this.strategy = strategy;
    }

    public void addInstance(ServiceInstance instance) {
        instances.add(instance);
    }

    public ServiceInstance getNextPeer() {
        return strategy.selectInstance(instances);
    }

    public void markAsDead(ServiceInstance secondServiceInstance) {
        secondServiceInstance.setAlive(false);
    }

    public void halthCheck() {
        for (ServiceInstance instance : instances) {
            instance.setAlive(ping(instance));
        }
    }

    protected boolean ping(ServiceInstance instance) {
        return Math.random() < (2.0 / 3.0);
    }

    public void balanceRequest(HttpRequest request, HttpResponse response) {
        if (instances.isEmpty()) {
            response.setStatusCode(503);
            response.setBody("No backends available");
            return;
        }

        int attempts = 0;
        while (attempts < MAX_ATTEMPTS) {
            ServiceInstance instance = getNextPeer();

            if (instance != null && instance.isAlive()) {
                boolean success = instance.serve(request, response);
                if (success) {
                    System.out.println("Served request from " + request.getPath() + " to " + instance.url);
                    return;
                }
            }
            System.out.println("Retrying request to " + request.getPath());
            attempts++;
        }

        response.setStatusCode(503);
        System.out.println("Service not available after retries");
        response.setBody("Service not available after retries");
    }
}
