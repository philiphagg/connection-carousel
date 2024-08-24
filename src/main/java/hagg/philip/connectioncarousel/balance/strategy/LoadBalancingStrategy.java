package hagg.philip.connectioncarousel.balance.strategy;

import hagg.philip.connectioncarousel.domain.ServiceInstance;
import org.springframework.stereotype.Component;

import java.util.List;

public interface LoadBalancingStrategy {
    ServiceInstance selectInstance(List<ServiceInstance> instances);

    String getName();
    boolean isActive();
    void setActive(boolean activate);

}
