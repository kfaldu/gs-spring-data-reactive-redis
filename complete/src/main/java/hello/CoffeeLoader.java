package hello;

import java.util.UUID;
import javax.annotation.PostConstruct;
import lombok.extern.java.Log;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
@Log
public class CoffeeLoader {

  private final ReactiveRedisConnectionFactory factory;
  private final ReactiveRedisTemplate<String, Coffee> coffeeOps;

  public CoffeeLoader(ReactiveRedisConnectionFactory factory,
      ReactiveRedisTemplate<String, Coffee> coffeeOps) {
    this.factory = factory;
    this.coffeeOps = coffeeOps;
  }

  @PostConstruct
  public void loadData() {
    factory.getReactiveConnection().serverCommands().flushAll().thenMany(
        Flux.range(0, (int) Math.pow(10, 3))
            .map(key -> new Coffee(UUID.randomUUID().toString(), "n:" + System.nanoTime()))
            .flatMap(coffee -> coffeeOps.opsForValue().set(coffee.getId(), coffee))
    ).subscribe();
  }
}
