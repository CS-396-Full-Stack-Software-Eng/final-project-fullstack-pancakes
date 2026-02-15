package backend.session;

import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Optional;
import java.util.LinkedHashMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class SessionService {
    private final SessionRepository sessionRepository;
    private final ObjectMapper mapper = new ObjectMapper();

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

    public Session createFakeSession(int partySize, String leaderName) {
        Session session = new Session();
        session.setPartySize(partySize);
        session.setParsingStatus(ParsingStatus.ACTIVE);
        session.setReceiptUrl("/images/sample-receipt.jpg");

        try {
            Map<String, String> users = new HashMap<>();
            users.put(UUID.randomUUID().toString(), leaderName);
            session.setUsers(mapper.writeValueAsString(users));

            Map<String, Map<String, Object>> items = new LinkedHashMap<>();
            items.put("item_1", Map.of("name", "Pizza", "price", 16.50, "claimedBy", ""));
            items.put("item_2", Map.of("name", "Sprite", "price", 2.00, "claimedBy", ""));
            session.setItems(mapper.writeValueAsString(items));
        } catch (Exception e) {
            throw new RuntimeException("could not serialize session data", e);
        }

        System.out.println("Hardcoded session created for: " + leaderName);
        return sessionRepository.save(session);
    }

    public Session claimItem(Long sessionId, String itemId, String userId) {
        System.out.println("user " + userId + " claimed item: " + itemId);
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("session not found"));

        try {
            Map<String, Map<String, Object>> items = mapper.readValue(
                    session.getItems(),
                    new TypeReference<Map<String, Map<String, Object>>>() {}
            );

            if (!items.containsKey(itemId)) {
                throw new RuntimeException("item not found");
            }

            Map<String, Object> item = items.get(itemId);
            item.put("claimedBy", userId);

            session.setItems(mapper.writeValueAsString(items));
            return sessionRepository.save(session);
        } catch (Exception e) {
            throw new RuntimeException("could not claim item", e);
        }
    }
}
