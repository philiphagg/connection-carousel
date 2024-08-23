package hagg.philip.connectioncarousel.balance;

import hagg.philip.connectioncarousel.balance.strategy.LoadBalancingStrategy;
import hagg.philip.connectioncarousel.domain.ServiceInstance;

import java.util.Arrays;
import java.util.Map;

class TestableServerPool extends ServerPool {
    private final Map<ServiceInstance, Boolean> mockPingResults;

    public TestableServerPool(Map<ServiceInstance, Boolean> mockPingResults, LoadBalancingStrategy strategy, ServiceInstance... instances) {
        super(Arrays.asList(instances), strategy);  // Pass the strategy to the parent constructor
        this.mockPingResults = mockPingResults;
    }

    @Override
    protected boolean ping(ServiceInstance instance) {
        return mockPingResults.getOrDefault(instance, true);  // Return the mock result
    }
}
