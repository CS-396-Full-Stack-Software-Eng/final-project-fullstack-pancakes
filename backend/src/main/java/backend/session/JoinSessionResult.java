package backend.session;

public class JoinSessionResult {
    private String newUserId; // match schema
    private Session session;

    public JoinSessionResult(String userId, Session session) {
        this.newUserId = userId;
        this.session = session;
    }

    public String getNewUserId() { return newUserId; }
    public Session getSession() { return session; }
}