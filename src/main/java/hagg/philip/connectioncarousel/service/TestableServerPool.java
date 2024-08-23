package hagg.philip.connectioncarousel.service;

import java.util.Map;

class TestableServerPool extends ServerPool {
    private final Map<ServiceInstance, Boolean> mockPingResults;

    public TestableServerPool(Map<ServiceInstance, Boolean> mockPingResults, ServiceInstance... instances) {
        super(instances);
        this.mockPingResults = mockPingResults;
    }

    @Override
    protected boolean ping(ServiceInstance instance) {
        // Use mocked result instead of random
        return mockPingResults.getOrDefault(instance, true);
    }
}
