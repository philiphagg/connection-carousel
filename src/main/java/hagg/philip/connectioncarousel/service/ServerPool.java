package hagg.philip.connectioncarousel.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class ServerPool {
    private final int MAX_ATTEMPTS = 3;
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

    public void roundRobinBalance(HttpRequest request, HttpResponse response){
        int attemtps = 0;
        while(attemtps < MAX_ATTEMPTS){
            ServiceInstance instance = getNextPeer();

            if(instance == null){
                response.setStatusCode(503);
                response.setBody("All services are dead");
                return;
            }
            if(instance.serve(request, response)){
                return;
            }
            markAsDead(instance);
            attemtps++;
        }
    }
}
