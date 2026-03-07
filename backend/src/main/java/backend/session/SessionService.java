package backend.session;

import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.proto.ReceiptParsingEvent;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Optional;
import java.util.LinkedHashMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class SessionService {
    private static final String RECEIPT_PARSING_EVENT_QUEUE = "receipt_parsing_event_queue";
    private final SessionRepository sessionRepository;
    private final ObjectMapper mapper = new ObjectMapper();
    private final RedisTemplate<String, byte[]> redis;

    public SessionService(SessionRepository sessionRepository, RedisTemplate<String, byte[]> redis) {
        this.sessionRepository = sessionRepository;
        this.redis = redis;
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

            // check if item is already claimed by someone else and throw error if so
            if (item.get("claimedBy") != null && !item.get("claimedBy").toString().isEmpty()) {
                throw new RuntimeException("item already claimed");
            }

            // if not, then try to claim the item, and throw error if receive one back (via catch logic)
            item.put("claimedBy", userId);
            session.setItems(mapper.writeValueAsString(items));
            return sessionRepository.save(session);
        } catch (Exception e) {
            // go through chain of errors to see if specifically because item was already claimed
            Throwable cause = e;
            while (cause != null) {
                String errorMessage = cause.getMessage();
                if (errorMessage != null && errorMessage.contains("Two users cannot claim the same item")) {
                    throw new RuntimeException("item already claimed", e);
                }
                cause = cause.getCause();
            }
            throw new RuntimeException("could not claim item", e);
        }
    }

    public Session uploadReceipt(String image, int partySize, String leaderName) {
        System.out.println(leaderName + " uploaded receipt");
        Session session = new Session(partySize);
        session.setParsingStatus(ParsingStatus.INITIALIZING);
    
        try {
            Map<String, String> users = new HashMap<>();
            users.put(UUID.randomUUID().toString(), leaderName);
            session.setUsers(mapper.writeValueAsString(users));
        } catch (Exception e) {
            throw new RuntimeException("could not serialize session users", e);
        }

        session = sessionRepository.save(session);

        String eventId = UUID.randomUUID().toString();
        long timestamp = System.currentTimeMillis();

        ReceiptParsingEvent event = ReceiptParsingEvent.newBuilder()
            .setEventId(eventId)
            .setSessionId(String.valueOf(session.getId()))
            .setImageUrl(image)
            .setStatus("INITIALIZING")
            .setCreatedAt(timestamp)
            .build();
            
        redis.opsForList().leftPush(RECEIPT_PARSING_EVENT_QUEUE, event.toByteArray()); 

        return session;
    }

    public Session addUserToSession(Long sessionId, String name) {
        System.out.println(name + " is joining session number " + sessionId);

        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("session not found"));

        try {
            Map<String, String> users = session.getUsers() != null 
                ? mapper.readValue(session.getUsers(), new TypeReference<Map<String, String>>() {})
                : new HashMap<>();

            users.put(UUID.randomUUID().toString(), name);
            session.setUsers(mapper.writeValueAsString(users));
            return sessionRepository.save(session);
        } catch (Exception e) {
            throw new RuntimeException("could not add user to session", e);
        }
    }
}
