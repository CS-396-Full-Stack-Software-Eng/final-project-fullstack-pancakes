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

  public Session() {
  }

  public Session(int partySize) {
    this.partySize = partySize;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getId() {
    return id;
  }

  public void setPartySize(int partySize) {
    this.partySize = partySize;
  }

  public int getPartySize() {
    return partySize;
  }
}
