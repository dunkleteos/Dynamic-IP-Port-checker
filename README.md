Project Documentation: Dynamic IP Port Checker
Dynamic IP Port Checker is an enterprise-grade network monitoring solution designed to audit infrastructure health using dynamic socket connections. By analyzing server dependencies at runtime, the system automatically builds a hierarchical topology model, tracks cascading failures, and delivers clear, actionable network status updates based on percentage-based error rates.

1. Architectural Design & Hierarchy
The system bypasses rigid configurations by parsing servers.txt at startup and dynamically categorizing infrastructure assets into three distinct, interconnected tiers based on their depth in the network tree:

MAIN SERVER: Root-level nodes with no upper dependencies (e.g., Core Switches, Main Corporate Routers). If all Main Servers fail, dependent sub-systems lose connectivity immediately. (Failure weight: 5)

MAJOR SERVER: Mid-tier bridge nodes that depend on a Main Server but host critical sub-systems of their own (e.g., Application Servers, API Gateways). (Failure weight: 3)

MINOR SERVER: Edge or leaf nodes with no independent sub-systems attached (e.g., Local Printers, Standalone Handheld Terminals, Local Database Instances). (Failure weight: 1)

Smart Cascading Failure Suppression & Redundancy
Cascade Block: If a parent node drops (DOWN), the system suppresses redundant ping commands for its children, automatically marking them as [BLOCKED] -> SKIPPED. This saves critical network I/O and prevents misleading alarms.

Network Redundancy: The architecture natively supports multi-parent high-availability paths (e.g., S1-S2). If a Major/Minor node is linked to two Main routers, it remains operational as long as at least one parent path stays healthy (UP).

2. Dynamic Alarm & Alert Rules
Instead of printing raw statistics or confusing metrics, the application evaluates the overall network health relative to the total infrastructure size, returning distinct dashboard alerts:

All Main Servers Down: Complete network blackout. Triggers 🚨🚨🚨 [CRITICAL RED ALERT] INFRASTRUCTURE BLACKOUT! ALL MAIN SERVERS COLLAPSED!

Error Rate ≥ 50%: Massive outage affecting half or more of the entire environment. Triggers 🚨🚨🚨 [CRITICAL RED ALERT] Massive outage detected! Immediate administrator response required!

Error Rate 30% - 50%: Significant infrastructure degradation. Triggers 🔶 [ORANGE ALERT] Significant degradation detected! Please check major components.

Error Rate > 0% and < 30%: Minor anomaly detected at the edge level while the core backbone remains stable. Triggers 🟢 [GREEN ALERT] System is self-healing.

Error Rate == 0%: Flawless status. Triggers ✅ [OK] System healthy. All monitored network systems are running flawlessly.

3. Step-by-Step User Guide
Step 1: Initialize the Configuration File
Create a plain text file named servers.txt in your project's target source directory (or update the exact local absolute path in Main.java). Populate the file using the standard comma-separated value (CSV) schema:

Plaintext
ID,Server_Name,IP_Address,Port,Parent_IDs
If a node has no parent, type NONE. For multiple redundancy parents, split them using a hyphen (e.g., S1-S2).

Sample Production Input (servers.txt):

Plaintext
S1,Main_Router_Primary,8.8.8.8,53,NONE
S2,Main_Router_Backup,127.0.0.1,9999,NONE
S3,Middle_App_Server,8.8.8.8,53,S1-S2
S4,Edge_Database,127.0.0.1,8888,S3
S5,Standalone_Device,8.8.8.8,53,NONE
(Note: Using invalid local addresses like 127.0.0.1:9999 allows you to easily simulate crash tests).

Step 2: Run the Application
Compile and execute the Main.java file. The console will immediately start in Topology Discovery Mode, read the configuration file, resolve the runtime hardware roles, and present a clear overview:

Plaintext
=== FAREVA AUTOMATED SERVER MONITORING SYSTEM STARTING ===

=== TOPOLOGY DISCOVERY ACTIVE ===
[TOPOLOGY] Server 'Main_Router_Primary' (S1) resolved as MAIN SERVER.
[TOPOLOGY] Server 'Main_Router_Backup' (S2) resolved as MAIN SERVER.
[TOPOLOGY] Server 'Middle_App_Server' (S3) resolved as MAJOR SERVER.
[TOPOLOGY] Server 'Edge_Database' (S4) resolved as MINOR SERVER.
[TOPOLOGY] Server 'Standalone_Device' (S5) resolved as MINOR SERVER.
=================================
Step 3: Live Verification & Testing
The monitoring engine polls your assets continuously every 5 seconds.

Live Reload: You do not need to restart the application to modify assets. If you update and save servers.txt, the engine will dynamically parse the new hardware mapping on the very next cycle.

Simulating Outages: If you purposely corrupt the IP addresses of S1 and S2 inside the text file, you will immediately witness S3 and S4 shifting into cascading SKIPPED mode while the central system shifts into a CRITICAL RED ALERT.
