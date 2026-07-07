import java.net.InetSocketAddress;
import java.net.Socket;

public class NetworkChecker {

    public static boolean pingPort(String ip, int port) {
        // HATA 2 ÇÖZÜMÜ: try-with-resources kullanılarak socket sızıntısı (leak) tamamen önlendi.
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(ip, port), 2000);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Double check (GMP standard)
    public static boolean gmpVerifyStatus(Server server) {
        boolean firstCheck = pingPort(server.ipAddress, server.port);
        if (firstCheck) { return true; }

        System.out.println("[WARNING] First check failed for " + server.serverName + ". Retrying in 2 seconds (GMP Protocol)...");

        try {
            // HATA 3 ÇÖZÜMÜ: Sıralı kontroldeki kilitlenme etkisini azaltmak için süre 2 saniyeye düşürüldü.
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            System.out.println("GMP Timer interrupted.");
        }
        return pingPort(server.ipAddress, server.port);
    }
}