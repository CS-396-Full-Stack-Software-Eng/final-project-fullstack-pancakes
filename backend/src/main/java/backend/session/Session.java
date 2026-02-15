package backend.session;

import jakarta.persistence.*;

@Entity
@Table(name = "SESSIONS")
public class Session {
  @Id
  @GeneratedValue
  @Column(name = "ID")
  private Long id;

  @Column(name = "PARTY_SIZE")
  private int partySize;

  public Session() {}

  public Session(int partySize) {
    this.partySize = partySize;
  }
}
