package hagg.philip.connectioncarousel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisReactiveAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@SpringBootApplication(exclude = {RedisAutoConfiguration.class, RedisRepositoriesAutoConfiguration.class, RedisReactiveAutoConfiguration.class})
public class ConnectionCarouselApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConnectionCarouselApplication.class, args);
    }

}
