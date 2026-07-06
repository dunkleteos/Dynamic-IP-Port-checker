public class CriticalServer extends Server {
    public CriticalServer(String id, String serverName, String ipAddress, int port, String parentId) {
        super(id, serverName, ipAddress, port, parentId);
    }

    @Override
    public int getRiskMultiplier() { return 3; }

    @Override
    public String getRiskLevel() { return "CRITICAL"; }
}
