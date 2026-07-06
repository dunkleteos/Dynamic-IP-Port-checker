# 🌐 Dynamic IP Port Checker

An enterprise-grade **network monitoring** application that dynamically audits infrastructure health using socket connections.

Instead of relying on static configurations, the application analyzes server relationships at runtime, automatically builds the network topology, detects cascading failures, and provides clear health reports based on percentage-based error rates.

---

# ✨ Features

- 🔍 Dynamic topology discovery from a configuration file
- 🌳 Automatic server hierarchy detection
- 🔗 Multi-parent redundancy support
- 🚫 Cascading failure suppression
- 📊 Percentage-based network health evaluation
- 🔄 Live configuration reload without restarting the application
- ⚡ Continuous monitoring every 5 seconds

---

# 🏗️ System Architecture

The application parses the `servers.txt` file at startup and automatically classifies every node into one of three infrastructure layers.

| Server Type | Description | Failure Weight |
|-------------|-------------|---------------:|
| **MAIN SERVER** | Root-level infrastructure nodes with no parent dependencies (Core Routers, Core Switches, etc.) | **5** |
| **MAJOR SERVER** | Mid-tier servers that depend on Main Servers while hosting their own child systems | **3** |
| **MINOR SERVER** | Leaf devices without dependent sub-systems | **1** |

---

# 🔄 Cascading Failure Protection

When a parent server becomes unavailable:

- Child servers are **not** pinged unnecessarily.
- They are automatically marked as:

```
[BLOCKED] -> SKIPPED
```

This:

- reduces unnecessary network traffic
- avoids false alarms
- speeds up monitoring

---

# 🔀 Network Redundancy

The monitoring engine supports **multiple parent connections**.

Example:

```
S3 → S1-S2
```

If either **S1** or **S2** is still online, **S3** remains operational.

This provides native support for high-availability network designs.

---

# 🚨 Alert Levels

The application evaluates the overall infrastructure health and generates dashboard-friendly alerts.

| Condition | Alert |
|-----------|-------|
| All Main Servers Down | 🚨 **CRITICAL RED ALERT** – Infrastructure Blackout |
| Error Rate ≥ 50% | 🚨 **CRITICAL RED ALERT** – Massive outage detected |
| Error Rate 30–50% | 🟠 **ORANGE ALERT** – Significant degradation |
| Error Rate 0–30% | 🟢 **GREEN ALERT** – Minor issues detected |
| Error Rate = 0% | ✅ **OK** – System healthy |

---

# 📁 Configuration

Create a file named:

```
servers.txt
```

Each line must follow this format:

```text
ID,Server_Name,IP_Address,Port,Parent_IDs
```

## Parent Rules

- Use `NONE` if the server has no parent.
- Use `-` to specify multiple parents.

Example:

```text
S1-S2
```

---

# 📝 Example Configuration

```text
S1,Main_Router_Primary,8.8.8.8,53,NONE
S2,Main_Router_Backup,127.0.0.1,9999,NONE
S3,Middle_App_Server,8.8.8.8,53,S1-S2
S4,Edge_Database,127.0.0.1,8888,S3
S5,Standalone_Device,8.8.8.8,53,NONE
```

> **Tip:** Using invalid local ports such as `127.0.0.1:9999` is useful for simulating server failures.

---

# ▶️ Running the Application

Compile and execute:

```text
Main.java
```

At startup the application automatically:

- Loads the configuration
- Discovers the topology
- Resolves server roles
- Starts continuous monitoring

Example output:

```text
=== FAREVA AUTOMATED SERVER MONITORING SYSTEM STARTING ===

=== TOPOLOGY DISCOVERY ACTIVE ===

[TOPOLOGY] Server 'Main_Router_Primary' (S1) resolved as MAIN SERVER.
[TOPOLOGY] Server 'Main_Router_Backup' (S2) resolved as MAIN SERVER.
[TOPOLOGY] Server 'Middle_App_Server' (S3) resolved as MAJOR SERVER.
[TOPOLOGY] Server 'Edge_Database' (S4) resolved as MINOR SERVER.
[TOPOLOGY] Server 'Standalone_Device' (S5) resolved as MINOR SERVER.
```

---

# 🔄 Live Monitoring

The monitoring engine performs health checks every **5 seconds**.

## Live Reload

No restart is required.

Simply modify and save `servers.txt`.

The application automatically reloads the updated topology during the next monitoring cycle.

---

# 🧪 Failure Simulation

To simulate a network outage:

1. Edit `servers.txt`
2. Replace the IP or port of one or more Main Servers with invalid values
3. Save the file

The application will automatically detect:

- Parent failures
- Cascading blocked nodes
- Updated network topology
- Appropriate alert level

---

# 📌 Technologies

- Java
- TCP Socket Connections
- File-Based Configuration
- Runtime Topology Discovery
- Dynamic Network Monitoring

---

# 📄 License

This project is intended for educational and demonstration purposes.
