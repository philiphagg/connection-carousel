package hagg.philip.connectioncarousel.config;

import hagg.philip.connectioncarousel.balance.ServerPool;
import hagg.philip.connectioncarousel.balance.strategy.LoadBalancingStrategy;
import hagg.philip.connectioncarousel.balance.strategy.RandomStrategy;
import hagg.philip.connectioncarousel.balance.strategy.RoundRobinStrategy;
import hagg.philip.connectioncarousel.domain.ServiceInstance;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URI;
import java.util.List;

@Configuration
public class AppConfig {
    @Bean
    public LoadBalancingStrategy roundRobinStrategy() {
        return new RoundRobinStrategy();
    }

    @Bean
    public LoadBalancingStrategy randomStrategy() {
        return new RandomStrategy();
    }

    @Bean
    @SneakyThrows
    public ServerPool serverPool(List<LoadBalancingStrategy> strategy) {
        ServiceInstance instance1 = new ServiceInstance(URI.create("http://www.instance.one.io").toURL());
        ServiceInstance instance2 = new ServiceInstance(URI.create("http://www.instance.two.io").toURL());
        ServiceInstance instance3 = new ServiceInstance(URI.create("http://www.instance.three.io").toURL());
        ServiceInstance instance4 = new ServiceInstance(URI.create("http://www.instance.four.io").toURL());
        return new ServerPool(strategy, instance1, instance2, instance3, instance4);
    }


}
