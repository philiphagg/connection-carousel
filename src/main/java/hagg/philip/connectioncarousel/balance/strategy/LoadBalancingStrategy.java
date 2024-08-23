package hagg.philip.connectioncarousel.balance.strategy;

import hagg.philip.connectioncarousel.domain.ServiceInstance;

import java.util.List;

public interface LoadBalancingStrategy {
    ServiceInstance selectInstance(List<ServiceInstance> instances);

}
