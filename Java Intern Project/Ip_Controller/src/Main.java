import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Main{
    public static void main(String[] args){
        System.out.println("=== FAREVA AUTOMATED SERVER MONITORING SYSTEM STARTING ===");

        String filePath = "C:\\Users\\ozgur\\Desktop\\java deneme\\Java Intern Project\\Ip_Controller\\src\\servers.txt";
        List<ServerInfo> serverList = new ArrayList<>();

        try{//using try catch to prevent any complete crashes
            List<String> lines = Files.readAllLines(Paths.get(filePath));
            for (String line : lines){
                if (line.trim().isEmpty()) continue; //skipping empty lines
                try {
                    String[] parts = line.split(",");
                    String name = parts[0].trim();
                    String ip = parts[1].trim();
                    int port = Integer.parseInt(parts[2].trim());
                    String risk = parts[3].trim();
                    serverList.add(new ServerInfo(name, ip, port, risk)); //putting txt infos into new ServerInfo object

                } catch (NumberFormatException e) {
                    System.out.println("[HATA] Port sayısı ayrıştırılamadı, satır atlanıyor: " + line);
                } catch (IndexOutOfBoundsException e) {
                    System.out.println("[HATA] Eksik veri alanı, satır atlanıyor: " + line);
                }
            }

        } catch (IOException e) {
            System.out.println("[CRITICAL ERROR] File could not be read!");
        }

        while(true){//Infinity loop to control every minute continuously
            int errorCounter=0,totalServer=0;
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");//adding date and time with this format
            String timestamp = LocalDateTime.now().format(dtf);

            System.out.println("Starting Scheduled Network Audit Cycle [" + timestamp + "]");

            for (ServerInfo server : serverList) {
                boolean isUp = NetworkChecker.gmpVerifyStatus(server);

                if (isUp) {
                    // Status 1: Connection Successful
                    System.out.println("[" + timestamp + "] [INFO] Server: " + server.serverName + " (" + server.ipAddress + ":" + server.port + ") -> STATUS: UP (1)");
                    totalServer++;
                } else {
                    // Status 0: Connection Failed after Double-Check (Deviation Detected)
                    System.out.println("[" + timestamp + "] [" + server.riskLevel + " DEVIATION] Server: " + server.serverName + " (" + server.ipAddress + ":" + server.port + ") -> STATUS: DOWN (0) !!!");
                    errorCounter++;
                    totalServer++;
                }
            }
            System.out.println("There are "+ totalServer+" servers, and "+errorCounter+" of them has an error.");
            System.out.println("Cycle Completed. Sleeping for 5 seconds\n");

            // Wait for 1 minute (60000 milliseconds) before the next check cycle
            try {
                Thread.sleep(5000);//temporary 5000 should be 60000
            } catch (InterruptedException e) {
                System.out.println("[ERROR] Monitoring loop interrupted.");
            }
        }
    }
}