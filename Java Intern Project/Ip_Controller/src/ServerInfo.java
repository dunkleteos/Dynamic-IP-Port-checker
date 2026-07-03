public class ServerInfo{
    public String serverName;
    public String ipAddress;
    public int port;
    public String riskLevel; // critical, major

    // Constructor
    public ServerInfo(String sn, String ip, int p, String rl) {
        serverName=sn;
        ipAddress=ip;
        port=p;
        riskLevel=rl;
    }
}