package backend.session;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import java.util.Optional;

@Controller
public class SessionController {

  private final SessionService service;
  private final ObjectMapper objectMapper = new ObjectMapper();

  public SessionController(SessionService service) {
    this.service = service;
  }

  // backend was not returning valid JSON,
  // so use SchemaMapping to turn {item={name=...}} to {item: {name: ... }}
  @SchemaMapping(typeName = "Session", field = "items")
  public String items(Session session) throws Exception {
    return session.getItems() != null ? objectMapper.writeValueAsString(session.getItems()) : null;
  }

  @SchemaMapping(typeName = "Session", field = "users")
  public String users(Session session) throws Exception {
    return session.getUsers() != null ? objectMapper.writeValueAsString(session.getUsers()) : null;
  }

  @QueryMapping
  public Optional<Session> getSessionById(@Argument Long id) {
    return service.getSessionById(id);
  }

  // function responsible for level 1 '/createSession' query
  @MutationMapping
  public Session createSession(@Argument int partySize) {
    return service.createSession(partySize);
  }

  // added for level 1 app
  @MutationMapping
  public Session uploadReceipt(@Argument String image, @Argument int partySize, @Argument String leaderName) {
    return service.createFakeSession(partySize, leaderName);
  }
}