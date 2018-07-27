package hello;

import java.nio.ByteBuffer;
import lombok.extern.java.Log;
import org.springframework.data.redis.connection.ReactiveKeyCommands;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.util.ByteUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@Log
public class CoffeeController {

  private final ReactiveRedisTemplate<String, Coffee> coffeTemplate;
  private final ReactiveKeyCommands keyCommands;

  CoffeeController(ReactiveRedisTemplate<String, Coffee> coffeeOps,
      ReactiveKeyCommands keyCommands) {
    this.coffeTemplate = coffeeOps;
    this.keyCommands = keyCommands;
  }

  private static String toString(ByteBuffer byteBuffer) {
    return new String(ByteUtils.getBytes(byteBuffer));
  }

  @GetMapping("/coffee/random")
  public Mono<Coffee> findRandom() {
    String randomKey = toString(keyCommands.randomKey().block());
    log.info("RandomKey: " + randomKey);
    return findOne(randomKey);
  }

  public Flux<Coffee> all() {
    return coffeTemplate.keys("*")
        .take(5)
        .flatMap(coffeTemplate.opsForValue()::get);
  }

  @GetMapping("/coffee/{id}")
  public Mono<Coffee> findOne(@PathVariable String id) {

    long start = System.nanoTime();
    Mono<Coffee> coffeeMono = coffeTemplate.keys(id)
        .flatMap(coffeTemplate.opsForValue()::get).next();
    long end = System.nanoTime();
    log.info("toal nano time " + (end - start));
    return coffeeMono;
  }
}
