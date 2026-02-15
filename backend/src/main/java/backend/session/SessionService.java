package backend.session;

import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Optional;
import java.util.Map;

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
    return sessionRepository.save(new Session(partySize));
  }

  // Parses the items JSONB string into a map and updates the item's owner_id
  public Session claimItem(Long sessionId, String itemId, String userId) {
    System.out.println("user " + userId + " claimed item: " + itemId);
    Session session = sessionRepository.findById(sessionId).orElseThrow(() -> new RuntimeException("session not found"));

    ObjectMapper mapper = new ObjectMapper();

    try {
      Map<String, Map<String, Object>> items = mapper.readValue(
        session.getItems(),
        new TypeReference<Map<String, Map<String, Object>>>() {}
      );

      if (!items.containsKey(itemId)) {
        throw new RuntimeException("item not found");
      }
      
      Map<String, Object> item = items.get(itemId);
      item.put("owner_id", userId);

      String updatedItems = mapper.writeValueAsString(items);
      session.setItems(updatedItems);

    return sessionRepository.save(session);
    } catch(Exception e) {
      throw new RuntimeException("could not claim item", e);
    }
  }
}