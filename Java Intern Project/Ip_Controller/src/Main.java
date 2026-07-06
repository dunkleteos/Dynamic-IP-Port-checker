import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Main{
    public static void main(String[] args){
        System.out.println("=== FAREVA AUTOMATED SERVER MONITORING SYSTEM STARTING ===");

        String filePath = "C:\\Users\\ozgur\\Desktop\\java deneme\\Java Intern Project\\Ip_Controller\\src\\servers.txt"; //This part depends on users file location
        List<Server> serverList = new ArrayList<>();

        //Reading File lines
        try {
            List<String> lines = Files.readAllLines(Paths.get(filePath));
            for(String line : lines){
                if(line.trim().isEmpty())
                    continue;
                try{
                    String[] parts = line.split(",");
                    String id = parts[0].trim();
                    String name = parts[1].trim();
                    String ip = parts[2].trim();
                    int port = Integer.parseInt(parts[3].trim());
                    String parentIdStr = parts[4].trim();

                    Server server = new Server(id, name, ip, port, parentIdStr);
                    serverList.add(server);
                }
                catch(Exception e){
                    System.out.println("[ERROR] Failed to parse line: " + line);
                }
            }
        }
        catch (IOException e){
            System.out.println("[CRITICAL ERROR] The configuration file could not be read!");
            return;
        }

        //Topology Discovery = Deciding which server is main, major or minor
        Set<String> activeParentIds = new HashSet<>();
        for (Server s : serverList){
            activeParentIds.addAll(s.parentIds);
        }

        System.out.println("\n=== TOPOLOGY DISCOVERY ACTIVE ===");
        int totalMasterCount = 0;
        for (Server s : serverList){
            boolean hasChildren = activeParentIds.contains(s.id);
            boolean hasParent = s.hasParents();

            if(hasChildren && !hasParent){
                s.upgradeServerRole("MAIN SERVER");
                totalMasterCount++;
            }else if(hasChildren && hasParent){
                s.upgradeServerRole("MAJOR SERVER");
            } else{
                s.upgradeServerRole("MINOR SERVER");
            }
            System.out.println("[TOPOLOGY] Server '" + s.serverName + "' (" + s.id + ") resolved as " + s.getRiskLevel() + ".");
        }
        System.out.println("======================================================================\n");

        // Infinity loop for checking servers every minute
        while (true) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");//Printing Year Mount
            String timestamp = LocalDateTime.now().format(dtf);

            System.out.println("--- Starting Scheduled Network Audit Cycle [" + timestamp + "] ---");

            Set<String> failedServerIds = new HashSet<>();
            int totalImpactScore = 0;
            int errorCounter = 0;
            int failedMasterCount = 0;

            for(Server server : serverList){

                // REDUNDANCY CHECK
                if (server.hasParents()) {
                    boolean allParentsFailed = true;
                    for (String pId : server.parentIds) {
                        if (!failedServerIds.contains(pId)) {
                            allParentsFailed = false;
                            break;
                        }
                    }

                    if (allParentsFailed) {
                        System.out.println("[" + timestamp + "] [BLOCKED] Server: " + server.serverName +
                                " -> SKIPPED (ALL backup parent paths " + server.parentIds + " are DOWN!)");

                        failedServerIds.add(server.id);
                        errorCounter++;
                        totalImpactScore += server.getRiskMultiplier();
                        continue;
                    }
                }

                // NETWORK AUDIT
                boolean isUp = NetworkChecker.gmpVerifyStatus(server);

                if(isUp){
                    System.out.println("[" + timestamp + "] [INFO] Server: " + server.serverName + " -> STATUS: UP (1)");
                }else{
                    System.out.println("[" + timestamp + "] [" + server.getRiskLevel() + " DEVIATION] Server: " + server.serverName + " -> STATUS: DOWN (0) !!!");

                    failedServerIds.add(server.id);
                    errorCounter++;
                    totalImpactScore += server.getRiskMultiplier();

                    if(server.getRiskLevel().equals("MAIN SERVER")){
                        failedMasterCount++;
                    }
                }
            }

            //Dynamic alarm system
            int totalServersCount = serverList.size();
            double errorRate = ((double) errorCounter / totalServersCount) * 100;

            System.out.println("\n================ SYSTEM STATUS UPDATE ================");

            //All main servers
            if (failedMasterCount > 0 && failedMasterCount == totalMasterCount) {
                System.out.println("🚨🚨🚨 [CRITICAL RED ALERT] INFRASTRUCTURE BLACKOUT! ALL MAIN SERVERS COLLAPSED! 🚨🚨🚨");
            }

            // 2. Durum: Sunucuların %50 veya daha fazlası çöktüyse
            else if (errorRate >= 50.0) {
                System.out.println("🚨🚨🚨 [CRITICAL RED ALERT] Massive outage detected! More than half of the infrastructure is down! Immediate administrator response required! 🚨🚨🚨");
            }

            // 3. Durum: Sunucuların %30 ile %50 arası çöktüyse
            else if (errorRate >= 30.0) {
                System.out.println("🔶 [ORANGE ALERT] Significant degradation detected! Multiple sub-systems are down. Please check major components.");
            }

            // 4. Durum: Sunucuların %0'dan fazla ama %30'dan azı çöktüyse
            else if (errorRate > 0.0) {
                System.out.println("🟢 [GREEN ALERT] Noticeable anomaly detected, but infrastructure remains stable. System is self-healing.");
            }

            // 5. Durum: Hata oranı tam olarak %0 ise (Her şey pırıl pırılsa)
            else {
                System.out.println("✅ [OK] System healthy. All monitored network systems are running flawlessly without any errors.");
            }

            System.out.println("======================================================\n");

            System.out.println("Cycle Completed. Sleeping for 5 seconds...\n");
            try{
                Thread.sleep(5000);
            }
            catch(InterruptedException e){}
        }

    }
}