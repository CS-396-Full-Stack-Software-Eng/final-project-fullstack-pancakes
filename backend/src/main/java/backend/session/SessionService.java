package backend.session;

import org.springframework.stereotype.Service;

import java.util.Optional;

// added for level 1
import java.util.LinkedHashMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class SessionService {
  private final SessionRepository sessionRepository;

  public SessionService(SessionRepository sessionRepository) {
    this.sessionRepository = sessionRepository;
  }

  public Optional<Session> getSessionById(Long sessionId) {
    return sessionRepository.findById(sessionId);
  }

  public Session createSession(int partySize) {
    System.out.println("session created");
    return sessionRepository.save(new Session(partySize));
  }

  // for app level 1
  public Session createFakeSession(int partySize, String leaderName) {
    Session session = new Session();
    session.setPartySize(partySize);

    // setting parsing status to active
    session.setParsingStatus(ParsingStatus.ACTIVE);
    session.setReceiptUrl("/images/sample-receipt.jpg"); 

    // initialize user list as the group leader
    Map<String, String> users = new HashMap<>();
    users.put(UUID.randomUUID().toString(), leaderName);
    session.setUsers(users);

    // hardcoded items on receipt
    Map<String, Map<String, Object>> items = new LinkedHashMap<>();
    items.put("item_1", Map.of("name", "Pizza", "price", 16.50, "claimedBy", "User1"));
    items.put("item_2", Map.of("name", "Sprite", "price", 2.00, "claimedBy", "User1"));
    session.setItems(items);
    System.out.println("Hardcoded session created for: " + leaderName);
    return sessionRepository.save(session);

  }
}