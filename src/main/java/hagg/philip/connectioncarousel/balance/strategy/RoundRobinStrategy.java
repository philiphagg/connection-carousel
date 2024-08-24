package hagg.philip.connectioncarousel.balance.strategy;

import hagg.philip.connectioncarousel.domain.ServiceInstance;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.List;

public class RoundRobinStrategy implements LoadBalancingStrategy {
    private int current = 0;

    @Getter
    public final String name = "Round Robin";
    @Getter @Setter
    public boolean isActive = true;

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
