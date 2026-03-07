package backend.session;

public class JoinSessionResult {
    private String userId;
    private Session session;

    public JoinSessionResult(String userId, Session session) {
        this.userId = userId;
        this.session = session;
    }

    public String getUserId() { return userId; }
    public Session getSession() { return session; }
}