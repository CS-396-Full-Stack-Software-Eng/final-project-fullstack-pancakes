package backend.session;

import jakarta.persistence.*;

// added for level 1
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.util.Map;

// added for level 1
enum ParsingStatus {
  INITIALIZING, PARSING, ACTIVE, CLOSED, FAILURE
}

@Entity
@Table(name = "SESSIONS")
public class Session {
  @Id
  @GeneratedValue
  @Column(name = "ID")
  private Long id;

  @Column(name = "PARTY_SIZE")
  private int partySize;

  // added for level 1
  @Enumerated(EnumType.STRING)
  private ParsingStatus parsingStatus;

  private String receiptUrl;
  
  // userId -> displayName
  @JdbcTypeCode(SqlTypes.JSON)
  private Map<String, String> users;

  // itemKey -> { name, price, claimedBy }
  @JdbcTypeCode(SqlTypes.JSON)
  private Map<String, Map<String, Object>> items;

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

  public ParsingStatus getParsingStatus() {
      return parsingStatus;
    }
  public void setParsingStatus(ParsingStatus parsingStatus) {
    this.parsingStatus = parsingStatus;
  }

  public String getReceiptUrl() {
    return receiptUrl;
  }

  public void setReceiptUrl(String receiptUrl) {
    this.receiptUrl = receiptUrl;
  }

  public Map<String, String> getUsers() {
    return users;
  }

  public void setUsers(Map<String, String> users) {
    this.users = users;
  }

  public Map<String, Map<String, Object>> getItems() {
    return items;
  }

  public void setItems(Map<String, Map<String, Object>> items) {
    this.items = items;
  }

}
