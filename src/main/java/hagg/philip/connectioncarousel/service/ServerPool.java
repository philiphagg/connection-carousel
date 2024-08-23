package hagg.philip.connectioncarousel.service;

import java.util.List;

public class ServerPool {
    List<ServiceInstance> instances;
    int current;

    public ServerPool(List<ServiceInstance> instances) {
        this.instances = instances;
        current = 0;
    }
    public ServerPool(ServiceInstance... instances) {
        this(List.of(instances));
    }

    public ServerPool() {
        this.instances = List.of();
        current = 0;
    }

    public void nextInstance() {
        current = (current + 1) % instances.size();
    }

    public void addInstance(ServiceInstance instance) {
        instances.add(instance);
    }

    public List<ServiceInstance> getInstances() {
        return instances;
    }

    public void setInstances(List<ServiceInstance> instances) {
        this.instances = instances;
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public ServiceInstance getNextPeer() {
        int startIndex = current;
        do {
            nextInstance();
            if (instances.get(current).isAlive()) {
                return instances.get(current);
            }
        } while (current != startIndex);
        return null;
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

    public void roundRobinBalance(){

    }
}
