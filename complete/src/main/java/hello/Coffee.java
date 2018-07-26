package hello;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Coffee {

  // not using UUID for purpose
  // we will have to create a uuid serializer
  // when we query from redis template - we will have to query for specific UUID
  // we will not be able to query by "*"
  private String id;
  private String name;
}
