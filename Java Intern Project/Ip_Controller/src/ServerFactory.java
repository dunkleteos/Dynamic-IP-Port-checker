public class ServerFactory {
    public static Server createServer(String id, String name, String ip, int port, String riskLevel, String parentId) {
        switch (riskLevel.trim().toUpperCase()) {
            case "CRITICAL":
                return new CriticalServer(id, name, ip, port, parentId);
            case "MAJOR":
                return new MajorServer(id, name, ip, port, parentId);
            case "MINOR":
            default:
                return new MinorServer(id, name, ip, port, parentId);
        }
    }
}
