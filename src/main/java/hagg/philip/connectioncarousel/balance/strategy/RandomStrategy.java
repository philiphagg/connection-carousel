package hagg.philip.connectioncarousel.balance.strategy;

import hagg.philip.connectioncarousel.domain.ServiceInstance;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class RandomStrategy implements LoadBalancingStrategy {

    @Getter
    public final String name = "Random";
    @Getter @Setter
    public boolean isActive;

    @Override
    public ServiceInstance selectInstance(List<ServiceInstance> instances) {
        return instances.get((int) (Math.random() * instances.size()));
    }


}
