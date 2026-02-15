package backend.session;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "SESSIONS")
public class Session {
  @Id
  @GeneratedValue
  @Column(name = "ID")
  private Long id;

  @Column(name = "party_size")
  private int partySize;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "users", columnDefinition = "jsonb")
  private String users;
  
  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "items", columnDefinition = "jsonb")
  private String items;

  public Session() {}

  public Session(int partySize) {
    this.partySize = partySize;
  }
  
  public String getItems() {
    return items;
  }

  public void setItems(String items) {
    this.items = items;
  }
    public String getUsers() {
    return users;
  }

  public void setUsers(String users) {
    this.users = users;
  }
}
