package hello;

import java.nio.ByteBuffer;
import java.util.UUID;
import javax.annotation.PostConstruct;
import lombok.extern.java.Log;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.util.ByteUtils;
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

  private static String toString(ByteBuffer byteBuffer) {
    return new String(ByteUtils.getBytes(byteBuffer));
  }

  @PostConstruct
  public void loadData() {
    factory.getReactiveConnection().serverCommands().flushAll().thenMany(
        Flux.range(0, (int) Math.pow(10, 3))
            .map(key -> new Coffee(UUID.randomUUID().toString(), "n:" + System.nanoTime()))
            .flatMap(coffee -> coffeeOps.opsForValue().set(coffee.getId(), coffee))
    ).subscribe();

    log.info(toString(factory.getReactiveConnection().keyCommands().randomKey().block()));

//    factory.getReactiveConnection().serverCommands().flushAll().thenMany(
//        Flux.just("Jet Black Redis", "Darth Redis", "Black Alert Redis")
//            .map(name -> new Coffee(UUID.randomUUID().toString(), name))
//            .flatMap(coffee -> coffeeOps.opsForValue().set(coffee.getId(), coffee)))
//        .thenMany(coffeeOps.keys("*")
//            .flatMap(coffeeOps.opsForValue()::get))
//        .subscribe(System.out::println);

  }
}
