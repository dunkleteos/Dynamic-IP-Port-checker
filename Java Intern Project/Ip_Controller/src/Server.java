import java.util.ArrayList;
import java.util.List;

public class Server {
    public String id;
    public String serverName;
    public String ipAddress;
    public int port;
    public List<String> parentIds = new ArrayList<>(); // Birden fazla parent desteği

    private int riskMultiplier = 1;
    private String riskLevel = "MINOR SERVER";

    public Server(String id, String serverName, String ipAddress, int port, String parentIdStr) {
        this.id = id.trim();
        this.serverName = serverName.trim();
        this.ipAddress = ipAddress.trim();
        this.port = port;

        // S1-S2 gibi çoklu bağımlılıkları ayırıp listeye ekliyoruz
        if (!parentIdStr.trim().equals("NONE")) {
            String[] parents = parentIdStr.split("-");
            for (String p : parents) {
                this.parentIds.add(p.trim());
            }
        }
    }

    public void upgradeServerRole(String newRole) {
        this.riskLevel = newRole;
        if (newRole.equals("MASTER SERVER")) { this.riskMultiplier = 5; }
        else if (newRole.equals("MAJOR SERVER")) { this.riskMultiplier = 3; }
        else { this.riskMultiplier = 1; }
    }

    public int getRiskMultiplier() { return this.riskMultiplier; }
    public String getRiskLevel() { return this.riskLevel; }
    public boolean hasParents() { return !this.parentIds.isEmpty(); }

    public int getTimeout() {
        if (this.riskLevel.equals("MASTER SERVER")) return 1500;
        if (this.riskLevel.equals("MAJOR SERVER")) return 2500;
        return 4000;
    }

    public int getDoubleCheckWaitTime() {
        if (this.riskLevel.equals("MASTER SERVER")) return 1;
        if (this.riskLevel.equals("MAJOR SERVER")) return 3000;
        return 0;
    }
}