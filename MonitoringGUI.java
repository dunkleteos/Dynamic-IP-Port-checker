import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MonitoringGUI extends JFrame
{
    private JTextArea logArea;
    private JButton btnStart, btnPauseResume, btnExit, btnEdit;

    // Core state control flags
    private volatile boolean isStarted = false;
    private volatile boolean isRunning = false;
    private volatile boolean isPaused = false;
    private Thread monitoringThread;

    // High-Contrast Color Palette
    private final Color bgDark = new Color(24, 24, 27);
    private final Color panelDark = new Color(39, 39, 42);
    private final Color terminalBlack = new Color(9, 9, 11);
    private final Color textNeonGreen = new Color(74, 222, 128);

    // Button Colors (Guaranteed Readability)
    private final Color colorGreen = new Color(34, 197, 94);   // Start Active
    private final Color colorOrange = new Color(245, 158, 11); // Pause
    private final Color colorCyan = new Color(6, 182, 212);    // Resume / Save
    private final Color colorRed = new Color(220, 38, 38);     // Exit
    private final Color colorMuted = new Color(45, 45, 50);    // Disabled/Running state

    public MonitoringGUI(){
        setTitle("Sanofi (Fareva) Lüleburgaz | Enterprise Network Monitor");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 1. Header Area
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(bgDark);
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("SANOFI (FAREVA) NETWORK AUDIT CONTROL CENTER");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JLabel locLabel = new JLabel("Lüleburgaz Plant - Production OT Network");
        locLabel.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        locLabel.setForeground(Color.GRAY);
        headerPanel.add(locLabel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // 2. Terminal Screen (Log Area)
        logArea = new JTextArea();
        logArea.setBackground(terminalBlack);
        logArea.setForeground(textNeonGreen);
        logArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        logArea.setEditable(false);
        logArea.setBorder(new EmptyBorder(12, 12, 12, 12));

        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(panelDark, 1));
        add(scrollPane, BorderLayout.CENTER);

        // 3. Control Toolbar
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        controlPanel.setBackground(bgDark);

        // Initializing Buttons
        btnStart = createStyledButton("START MONITORING", colorGreen, Color.WHITE);
        btnPauseResume = createStyledButton("PAUSE SYSTEM", colorMuted, new Color(130, 130, 135));
        btnEdit = createStyledButton("EDIT SERVERS", colorMuted, Color.WHITE);
        btnExit = createStyledButton("EXIT SIMULATION", colorRed, Color.WHITE);

        controlPanel.add(btnStart);
        controlPanel.add(btnPauseResume);
        controlPanel.add(btnEdit);
        controlPanel.add(btnExit);
        add(controlPanel, BorderLayout.SOUTH);

        // 4. Dynamic Button Event Listeners

        // START BUTTON
        btnStart.addActionListener(e ->
        {
            if (isStarted){
                return;
            }
            isStarted = true;
            isRunning = true;
            isPaused = false;
            logArea.setText(""); // Fresh clear logs

            btnStart.setText("SYSTEM RUNNING...");
            btnStart.setBackground(colorMuted);
            btnStart.setForeground(new Color(150, 150, 155));

            btnPauseResume.setText("PAUSE");
            btnPauseResume.setBackground(colorOrange);
            btnPauseResume.setForeground(Color.BLACK);

            monitoringThread = new Thread(this::startMonitoringLogic);
            monitoringThread.start();
        });

        // DYNAMIC PAUSE / RESUME TOGGLE BUTTON
        btnPauseResume.addActionListener(e -> {
            if (!isStarted || !isRunning) return;

            if (!isPaused) {
                isPaused = true;
                btnPauseResume.setText("RESUME");
                btnPauseResume.setBackground(colorCyan);
                btnPauseResume.setForeground(Color.BLACK);
                appendLog("\n[SYSTEM CONTROL] ⏸️ Audit cycle PAUSED by administrator.");
            } else {
                isPaused = false;
                btnPauseResume.setText("PAUSE");
                btnPauseResume.setBackground(colorOrange);
                btnPauseResume.setForeground(Color.BLACK);
                appendLog("[SYSTEM CONTROL] ▶️ Audit cycle RESUMED successfully.\n");
            }
        });

        // EDIT BUTTON
        btnEdit.addActionListener(e -> {
            if (isStarted && !isPaused) {
                JOptionPane.showMessageDialog(this,
                        "Please pause or stop the system before editing the server configuration!",
                        "System Warning", JOptionPane.WARNING_MESSAGE);
            } else {
                openEditor();
            }
        });

        // DIRECT EXIT BUTTON
        btnExit.addActionListener(e -> {
            System.exit(0);
        });
    }

    // Server Configuration Editor Window
    private void openEditor() {
        JDialog editorDialog = new JDialog(this, "Edit Server Configuration (servers.txt)", true);
        editorDialog.setSize(650, 450);
        editorDialog.setLayout(new BorderLayout());

        JTextArea editArea = new JTextArea();
        editArea.setBackground(terminalBlack);
        editArea.setForeground(textNeonGreen);
        editArea.setCaretColor(Color.WHITE);
        editArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        editArea.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Read file content
        try {
            String content = new String(Files.readAllBytes(Paths.get("servers.txt")));
            editArea.setText(content);
        } catch (IOException e) {
            editArea.setText("// [ERROR] 'servers.txt' file not found!\n// Please make sure the file exists in the project root directory.");
        }

        JScrollPane scrollPane = new JScrollPane(editArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(panelDark, 2));
        editorDialog.add(scrollPane, BorderLayout.CENTER);

        // Save Button
        JButton btnSave = createStyledButton("SAVE & APPLY", colorCyan, Color.BLACK);
        btnSave.addActionListener(e -> {
            try {
                Files.write(Paths.get("servers.txt"), editArea.getText().getBytes());
                JOptionPane.showMessageDialog(editorDialog,
                        "Configuration saved successfully! The system will reload this list on the next audit cycle.",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                editorDialog.dispose();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(editorDialog, "An error occurred while saving the file!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(bgDark);
        bottomPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        bottomPanel.add(btnSave);
        editorDialog.add(bottomPanel, BorderLayout.SOUTH);

        editorDialog.setLocationRelativeTo(this);
        editorDialog.setVisible(true);
    }

    private JButton createStyledButton(String text, Color bgColor, Color fgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(bgColor);
        btn.setForeground(fgColor);
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setBorder(BorderFactory.createLineBorder(bgColor.darker(), 1));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public void appendLog(String message) {
        SwingUtilities.invokeLater(() -> {
            logArea.append(message + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    private void startMonitoringLogic() {
        appendLog("=== SANOFI(FAREVA) AUTOMATED SERVER MONITORING SYSTEM INITIALIZING ===");

        String filePath = "servers.txt";
        List<Server> serverList = new ArrayList<>();

        try {
            List<String> lines = Files.readAllLines(Paths.get(filePath));
            for (String line : lines) {
                if (line.trim().isEmpty()) continue;
                try {
                    String[] parts = line.split(",");
                    String id = parts[0].trim();
                    String name = parts[1].trim();
                    String ip = parts[2].trim();
                    int port = Integer.parseInt(parts[3].trim());
                    String parentIdStr = parts[4].trim();

                    Server server = new Server(id, name, ip, port, parentIdStr);
                    serverList.add(server);
                } catch (Exception e) {
                    appendLog("[ERROR] Failed to parse configuration line: " + line);
                }
            }
        } catch (IOException e) {
            appendLog("[CRITICAL ERROR] Infrastructure configuration file could not be read! Check path: " + Paths.get(filePath).toAbsolutePath());
            return;
        }

        Set<String> activeParentIds = new HashSet<>();
        for (Server s : serverList) {
            activeParentIds.addAll(s.parentIds);
        }

        appendLog("\n=== STARTING AUTOMATED TOPOLOGY DISCOVERY ===");
        int totalMainCount = 0;
        List<Server> polymorphicServerList = new ArrayList<>();

        for (Server s : serverList) {
            boolean hasChildren = activeParentIds.contains(s.id);
            boolean hasParent = s.hasParents();

            String resolvedRole;
            if (hasChildren && !hasParent) {
                resolvedRole = "MAIN SERVER";
                totalMainCount++;
            } else if (hasChildren && hasParent) {
                resolvedRole = "MAJOR SERVER";
            } else {
                resolvedRole = "MINOR SERVER";
            }

            String parentIdStr = s.parentIds.isEmpty() ? "NONE" : String.join("-", s.parentIds);
            Server polymorphicServer = ServerFactory.createServer(s.id, s.serverName, s.ipAddress, s.port, resolvedRole, parentIdStr);
            polymorphicServerList.add(polymorphicServer);

            appendLog("[TOPOLOGY] Node '" + polymorphicServer.serverName + "' (" + polymorphicServer.id + ") mapped as " + polymorphicServer.getRiskLevel() + ".");
        }

        serverList = polymorphicServerList;
        appendLog("======================================================================\n");

        while (isRunning){
            if (isPaused){
                try{
                    Thread.sleep(400);
                    continue;
                }catch (InterruptedException e) {
                    break;
                }
            }

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String timestamp = LocalDateTime.now().format(dtf);

            appendLog("--- Starting Scheduled Network Audit Cycle [" + timestamp + "] ---");

            Set<String> failedServerIds = new HashSet<>();
            int totalImpactScore = 0;
            int errorCounter = 0;
            int failedMainCount = 0;

            for (Server server : serverList){
                if (!isRunning || isPaused) break;

                if (server.hasParents()) {
                    boolean allParentsFailed = true;
                    for (String pId : server.parentIds) {
                        if (!failedServerIds.contains(pId)) {
                            allParentsFailed = false;
                            break;
                        }
                    }

                    if (allParentsFailed){
                        appendLog("[" + timestamp + "] [BLOCKED] Node: " + server.serverName +
                                " -> SKIPPED (All backup parent paths " + server.parentIds + " are DOWN!)");

                        failedServerIds.add(server.id);
                        errorCounter++;
                        totalImpactScore += server.getRiskMultiplier();
                        continue;
                    }
                }

                boolean isUp = NetworkChecker.gmpVerifyStatus(server);

                if (isUp){
                    appendLog("[" + timestamp + "] [INFO] Node: " + server.serverName + " -> STATUS: UP (1)");
                } else {
                    appendLog("[" + timestamp + "] [" + server.getRiskLevel() + " DEVIATION] Node: " + server.serverName + " -> STATUS: DOWN (0) !!!");

                    failedServerIds.add(server.id);
                    errorCounter++;
                    totalImpactScore += server.getRiskMultiplier();

                    if (server.getRiskLevel().equals("MAIN SERVER")) {
                        failedMainCount++;
                    }
                }
            }

            if (!isRunning) break;

            int totalServersCount = serverList.size();
            double errorRate = ((double) errorCounter / totalServersCount) * 100;

            appendLog("\n================ INDUSTRIAL STATUS UPDATE ================");

            if (failedMainCount > 0 && failedMainCount == totalMainCount) {
                appendLog("🚨🚨🚨 [CRITICAL RED ALERT] INFRASTRUCTURE BLACKOUT! ALL MAIN SERVERS COLLAPSED! 🚨🚨🚨");
            } else if (errorRate >= 50.0) {
                appendLog("🚨🚨🚨 [CRITICAL RED ALERT] Massive outage detected! >50% of infrastructure is down! Immediate OT response required! 🚨🚨🚨");
            } else if (errorRate >= 30.0) {
                appendLog("🔶 [ORANGE ALERT] Significant degradation! Multiple sub-systems are down. Check major industrial switches.");
            } else if (errorRate > 0.0) {
                appendLog("🟢 [GREEN ALERT] Minor anomaly detected, but infrastructure remains operational. Self-healing protocols active.");
            } else {
                appendLog("✅ [OK] Infrastructure healthy. All critical network segments operating with 0% error rate.");
            }

            appendLog("==========================================================\n");
            appendLog("Cycle Completed. Standing by for 1 minute...\n");

            try {
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
