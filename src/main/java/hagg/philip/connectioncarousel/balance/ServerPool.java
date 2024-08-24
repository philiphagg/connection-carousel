package hagg.philip.connectioncarousel.balance;

import hagg.philip.connectioncarousel.balance.strategy.LoadBalancingStrategy;
import hagg.philip.connectioncarousel.domain.HttpRequest;
import hagg.philip.connectioncarousel.domain.HttpResponse;
import hagg.philip.connectioncarousel.domain.NoActiveStrategyException;
import hagg.philip.connectioncarousel.domain.ServiceInstance;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class ServerPool {
    private final int MAX_ATTEMPTS = 3;

    @Getter
    List<ServiceInstance> instances;
    private final List<LoadBalancingStrategy> strategies;

    @Autowired
    public ServerPool(List<ServiceInstance> instances, List<LoadBalancingStrategy> strategies) {
        this.instances = new ArrayList<>(instances);
        this.strategies = strategies;
    }

    public ServerPool(List<LoadBalancingStrategy> strategies, ServiceInstance... instances) {
        this(new ArrayList<>(Arrays.asList(instances)), strategies);
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


    public void addInstance(ServiceInstance instance) {
        instances.add(instance);
    }

    public void removeInstance(String url) {
        instances.removeIf(instance -> String.valueOf(instance.getUrl()).equals(url));
    }

    public ServiceInstance getNextPeer() {
        return strategies.stream()
                .filter(LoadBalancingStrategy::isActive)
                .findFirst()
                .orElseThrow(NoActiveStrategyException::new)
                .selectInstance(instances);
    }

    public void markAsDead(ServiceInstance instance) {
        instance.setAlive(false);
    }

    public void halthCheck() {
        for (ServiceInstance instance : instances) {
            instance.setAlive(ping(instance));
        }
    }

    public boolean ping(ServiceInstance instance) {
        return Math.random() < (2.0 / 3.0);
    }

}
