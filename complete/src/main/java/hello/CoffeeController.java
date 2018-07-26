package hello;

import lombok.extern.java.Log;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@Log
public class CoffeeController {

  private final ReactiveRedisTemplate<String, Coffee> coffeTemplate;

  CoffeeController(ReactiveRedisTemplate<String, Coffee> coffeeOps) {
    this.coffeTemplate = coffeeOps;
  }

  @GetMapping("/coffees")
  public Flux<Coffee> all() {
    return coffeTemplate.keys("*")
        .flatMap(coffeTemplate.opsForValue()::get);
  }

  @GetMapping("/coffee/{id}")
  public Mono<Coffee> findOne(@PathVariable String id) {
    long start = System.nanoTime();
    Mono<Coffee> coffeeMono = coffeTemplate.keys(id)
        .flatMap(coffeTemplate.opsForValue()::get).next();
    long end = System.nanoTime();
    log.info("toal nano time "+ (end - start));
    return coffeeMono;
  }
}
