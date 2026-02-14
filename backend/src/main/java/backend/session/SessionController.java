package backend.session;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.Optional;

@Controller
public class SessionController {

  private final SessionService service;

  public SessionController(SessionService service) {
    this.service = service;
  }

  @QueryMapping
  public Optional<Session> getSessionById(@Argument Long id) {
    return service.getSessionById(id);
  }
}