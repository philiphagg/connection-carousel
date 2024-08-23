package hagg.philip.connectioncarousel.balance.strategy;

import hagg.philip.connectioncarousel.domain.ServiceInstance;

import java.util.List;

public class RoundRobinStrategy implements LoadBalancingStrategy {
    private int current = 0;

    @Override
    public ServiceInstance selectInstance(List<ServiceInstance> instances) {
        int startIndex = current;
        do {
            current = (current + 1) % instances.size();
            if (instances.get(current).isAlive()) {
                return instances.get(current);
            }
        } while (current != startIndex);
        return null;
    }
}
