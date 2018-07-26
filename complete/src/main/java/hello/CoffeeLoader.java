package hello;

import java.util.UUID;
import javax.annotation.PostConstruct;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
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

    Flux<Coffee> coffeeFlux = Flux.create(fluxSink -> {
      for (long i = 0; i < Math.pow(10, 7); ++i) {
        fluxSink.next(new Coffee(UUID.randomUUID().toString(), "n:" + System.nanoTime()));
      }
    });

    factory.getReactiveConnection().serverCommands().flushAll().thenMany(
        coffeeFlux
            .flatMap(coffee -> coffeeOps.opsForValue().set(coffee.getId(), coffee)))
        .thenMany(coffeeOps.keys("*")
            .flatMap(coffeeOps.opsForValue()::get))
        .subscribe();

//    factory.getReactiveConnection().serverCommands().flushAll().thenMany(
//        Flux.just("Jet Black Redis", "Darth Redis", "Black Alert Redis")
//            .map(name -> new Coffee(UUID.randomUUID().toString(), name))
//            .flatMap(coffee -> coffeeOps.opsForValue().set(coffee.getId(), coffee)))
//        .thenMany(coffeeOps.keys("*")
//            .flatMap(coffeeOps.opsForValue()::get))
//        .subscribe(System.out::println);

  }
}
