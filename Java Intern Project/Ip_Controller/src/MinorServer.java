public class MinorServer extends Server {
    public MinorServer(String id, String serverName, String ipAddress, int port, String parentId) {
        super(id, serverName, ipAddress, port, parentId);
    }

    @Override
    public int getRiskMultiplier(){
        return 1;
    }

    @Override
    public String getRiskLevel() { return "MINOR"; }
}
