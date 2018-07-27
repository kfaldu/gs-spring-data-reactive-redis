package hello;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveKeyCommands;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class CoffeeConfiguration {

  @Bean
  ReactiveRedisTemplate<String, Coffee> redisOperations(ReactiveRedisConnectionFactory factory) {
    Jackson2JsonRedisSerializer<Coffee> coffeeSerializer = new Jackson2JsonRedisSerializer<>(
        Coffee.class);
    StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();

    RedisSerializationContext<String, Coffee> serializationContext = RedisSerializationContext
        .<String, Coffee>newSerializationContext()
        .key(stringRedisSerializer)
        .value(coffeeSerializer)
        .hashKey(stringRedisSerializer)
        .hashValue(coffeeSerializer)
        .build();

    return new ReactiveRedisTemplate<>(factory, serializationContext);
  }

  @Bean
  ReactiveKeyCommands reactiveKeyCommands(ReactiveRedisConnectionFactory factory) {
    return factory.getReactiveConnection().keyCommands();
  }

}
