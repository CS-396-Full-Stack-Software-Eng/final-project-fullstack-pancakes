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

    @Enumerated(EnumType.STRING)
    private ParsingStatus parsingStatus;

    private String receiptUrl;

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
