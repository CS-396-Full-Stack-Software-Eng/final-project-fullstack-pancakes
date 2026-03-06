package backend.worker;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;

import com.example.proto.ReceiptParsingEvent;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.boot.context.event.ApplicationReadyEvent;

import backend.session.Session;
import backend.session.SessionRepository;
import backend.session.ParsingStatus;
import backend.service.MockReceiptParsingService;


@Component
public class ReceiptParsingWorker {
  private final RedisTemplate<String, byte[]> redis;
  private final SimpMessagingTemplate socket;
  private final MockReceiptParsingService mockParsingService;
  private final SessionRepository sessionRepository;
  private final ObjectMapper mapper = new ObjectMapper();


public ReceiptParsingWorker(RedisTemplate<String, byte[]> redis, SimpMessagingTemplate socket, MockReceiptParsingService mockParsingService, SessionRepository sessionRepository) {
    this.redis = redis;
    this.socket = socket;
    this.mockParsingService = mockParsingService;
    this.sessionRepository = sessionRepository;
}

  @EventListener(ApplicationReadyEvent.class)
  public void startWorker() {
    new Thread(() -> {
      System.out
          .println("worker is listening for events");
      while (true) {
        try {
          byte[] payload = redis.opsForList().rightPop("receipt_parsing_event_queue", Duration.ofSeconds(30));

          if (payload != null) {
            ReceiptParsingEvent event = ReceiptParsingEvent.parseFrom(payload);
            System.out.println("worker received event with ID: " + event.getEventId());
            parseReceipt(event);
          }
        } catch (Exception e) {
          if (e.getMessage() != null && e.getMessage().contains("timed out")) {
            continue;
          }
          System.err.println("There was an error processing event: " + e.getMessage());
          e.printStackTrace();
        }
      }
    }).start();
  }

  private void parseReceipt(ReceiptParsingEvent event) {
    String sessionId = event.getSessionId();

    Session session = sessionRepository.findById(Long.valueOf(sessionId)).orElseThrow();
    session.setParsingStatus(ParsingStatus.PARSING);
    sessionRepository.save(session);
    
    // give the frontend some time to navigate and subscribe
    try {
    Thread.sleep(1500); 
    } catch (InterruptedException e) {}

    socket.convertAndSend("/topic/session/" + sessionId, (Object) Map.of(
      "sessionId", sessionId,
      "status", ParsingStatus.PARSING
      ));

    try {
        // call mock parsing service (not using grpc yet)
        Map<String, Map<String, Object>> items = mockParsingService.parseReceipt(event.getImageUrl());

        session.setItems(mapper.writeValueAsString(items));
        sessionRepository.save(session);

        session.setParsingStatus(ParsingStatus.ACTIVE);
        sessionRepository.save(session);

        socket.convertAndSend("/topic/session/" + sessionId, (Object) Map.of(
            "status", ParsingStatus.ACTIVE,
            "sessionId", sessionId,
            "items", items
        ));

    } catch (Exception e) {
      session.setParsingStatus(ParsingStatus.FAILURE);
      sessionRepository.save(session);
      
        socket.convertAndSend("/topic/session/" + sessionId, (Object) Map.of(
            "status", ParsingStatus.FAILURE,
            "sessionId", sessionId,
            "message", "Receipt could not be parsed"
        ));
      }
  }
}
