package backend.session;

import org.springframework.stereotype.Service;

import java.util.Optional;

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
}