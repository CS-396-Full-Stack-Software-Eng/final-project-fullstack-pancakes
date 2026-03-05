package backend.session;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
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

    @MutationMapping
    public Session createSession(@Argument int partySize) {
        return service.createSession(partySize);
    }

    @MutationMapping
    public Session uploadReceipt(@Argument String image, @Argument int partySize, @Argument String leaderName) {
        return service.uploadReceipt(image, partySize, leaderName);
    }

    @MutationMapping
    public Session claimItem(@Argument Long sessionId, @Argument String itemId, @Argument String userId) {
        return service.claimItem(sessionId, itemId, userId);
    }
}
