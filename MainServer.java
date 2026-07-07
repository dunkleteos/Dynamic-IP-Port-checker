// MainServer.java
public class MainServer extends Server {
    public MainServer(String id, String serverName, String ipAddress, int port, String parentId) {
        super(id, serverName, ipAddress, port, parentId);
    }

    @Override
    public int getRiskMultiplier() { return 5; }

    @Override
    public String getRiskLevel() { return "MAIN SERVER"; } // Main.java kontrolüyle eşitlendi
}