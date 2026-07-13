# 🌐 Sanofi Enterprise Network Monitor (Dynamic IP Port Checker)

An enterprise-grade, industrial network monitoring control center that dynamically audits infrastructure health using asynchronous socket connections. 

Moving away from static console logs, this version features a custom high-contrast **Industrial Dark Mode GUI** powered by a zero-lag multi-threaded architecture. The application automatically analyzes server dependencies at runtime, builds a dynamic network topology, prevents false-alarm cascading failures, and provides real-time infrastructure alerts.

---
<img width="1122" height="825" alt="image" src="https://github.com/user-attachments/assets/f13347fe-7c40-41e1-94d2-ee2d8f9d3faf" />


## ✨ Key Features

* 🎨 **Industrial Dark Mode GUI:** High-contrast, custom-rendered user interface designed for 24/7 production environments (bypasses default OS theme restrictions for perfect color consistency).
* ⚡ **Asynchronous Thread Management:** Tightly decoupled architecture where the network monitoring engine runs on a dedicated background worker thread, ensuring the main user interface **never freezes**.
* 🎛️ **Dynamic Legibility Controls:** Interactive Start/Pause/Resume action buttons that dynamically adjust foreground contrast (e.g., automated transition to rich black text on vibrant warning backgrounds) to guarantee flawless legibility.
* 🔍 **Dynamic Topology Discovery:** Generates an live network graph directly from an external text configuration file at runtime.
* 🌳 **Hierarchical Node Classification:** Automatically calculates and reflects risks based on server dependencies.
* 🔗 **Multi-Parent Redundancy Support:** Out-of-the-box support for high-availability cluster topologies.
* 🚫 **Cascading Outage Suppression:** Intelligently isolates dropped parent paths and skips down-stream nodes, eliminating network floods and alert fatigue.

---

## 🏗️ Infrastructure Hierarchy & Classification

The system parses the environment layout at initialization and classifies each node into one of three distinct layers:

| Server Type | Description | Failure Weight (Impact) |
| :--- | :--- | :---: |
| **MAIN SERVER** | Root-level core infrastructure with no upstream dependencies (e.g., Core Routers, Data Centers). | **5** |
| **MAJOR SERVER** | Mid-tier operational infrastructure depending on Main systems while serving sub-nodes (e.g., Central Switches). | **3** |
| **MINOR SERVER** | Leaf-level production devices or machinery terminal endpoints with no dependents (e.g., PLC, Scales). | **1** |

---

## 🎀 Core Logic Operations

### 1. Cascading Outage Suppression (Smart Skipping)
When a critical backbone connection goes down, the engine suppresses ping loops for dependent child systems:
* Unnecessary network floods are prevented.
* Child nodes are automatically logged as: `[BLOCKED] -> SKIPPED (All backup parent paths are DOWN!)`

### 2. High-Availability Redundancy
Nodes can accept multiple parent IDs mapped via a hyphenDelimiter (e.g., `S1-S2`). If `S1` undergoes a blackout but `S2` remains operational, the monitoring graph maintains the connection route seamlessly.

---

## 🚨 Automated Alert Matrix

Overall infrastructure integrity is calculated every cycle based on mathematical impact rates:

| Operational Condition | Visual Dashboard Alert Level |
| :--- | :--- |
| **All Main Servers Down** | 🚨 **CRITICAL RED ALERT** – Total Infrastructure Blackout! |
| **Weighted Error Rate ≥ 50%** | 🚨 **CRITICAL RED ALERT** – Massive Outage! Action Required! |
| **Weighted Error Rate 30% – 49%**| 🔶 **ORANGE ALERT** – Significant System Degradation! |
| **Weighted Error Rate 1% – 29%** | 🟢 **GREEN ALERT** – Minor Deviations Active (Self-Healing). |
| **Weighted Error Rate = 0%** | ✅ **SYSTEM OK** – Infrastructure fully functional. |

---

## 📁 Environment Setup (`servers.txt`)

Create a deployment schema map named `servers.txt` in your local project root path. Data entries must strictly adhere to the following comma-separated value formatting:

```text
ID, Server_Name, IP_Address/Domain, Port, Parent_IDs
