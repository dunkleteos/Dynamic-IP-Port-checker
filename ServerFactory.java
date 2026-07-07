public class ServerFactory{
    public static Server createServer(String id, String name, String ip, int port, String riskLevel, String parentId) {
        // Hem kısa ("MAIN") hem uzun ("MAIN SERVER") rolleri destekleyecek şekilde güncellendi.
        switch (riskLevel.trim().toUpperCase()) {
            case "MAIN":
            case "MAIN SERVER":
                return new MainServer(id, name, ip, port, parentId);
            case "MAJOR":
            case "MAJOR SERVER":
                return new MajorServer(id, name, ip, port, parentId);
            case "MINOR":
            case "MINOR SERVER":
            default:
                return new MinorServer(id, name, ip, port, parentId);
        }
    }
}