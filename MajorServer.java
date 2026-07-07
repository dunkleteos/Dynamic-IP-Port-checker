// MajorServer.java
public class MajorServer extends Server {
    public MajorServer(String id, String serverName, String ipAddress, int port, String parentId) {
        super(id, serverName, ipAddress, port, parentId);
    }

    @Override
    public int getRiskMultiplier() { return 3; }

    @Override
    public String getRiskLevel() { return "MAJOR SERVER"; } // Main.java kontrolüyle eşitlendi
}